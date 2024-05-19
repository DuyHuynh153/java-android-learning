package com.example.bai_3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Stack;

public class Activity_1 extends AppCompatActivity {
    private File currentDirectory;
    private Stack<File> directoryStack = new Stack<>();
//    private ArrayAdapter<String> adapter;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

//    private ArrayList<String> arrayList;
    private ListView listView;
    private TextView textLocation;
    //
    private int selectedItem = -1; // -1 indicates no item is selected

    private CustomArrayAdapter customArrayAdapter;


    public void goToActivity2(View view) {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        listView = findViewById(R.id.listView);
        Button buttonUp = findViewById(R.id.buttonUp);
        Button buttonSelect = findViewById(R.id.buttonSelect);
        Button buttonExit = findViewById(R.id.buttonExit);
        textLocation = findViewById(R.id.textLocation);

        // request permistion to accces files, photo, picture, music
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Activity_1.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(Activity_1.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(Activity_1.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
//            doStuff();

//            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            customArrayAdapter = new CustomArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

            currentDirectory = Environment.getExternalStorageDirectory(); // start at root directory
            textLocation.setText(currentDirectory.getPath());
            Log.e("root path", currentDirectory.getPath());
            listFile();
        }

        buttonUp.setOnClickListener(v -> {
            // do something with the selected file
        });
        buttonSelect.setOnClickListener(v -> {
            // implement logic to play all music files in current directory
        });

        buttonExit.setOnClickListener(v -> {
            finish();
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFile = customArrayAdapter.getItem(position);
                File clickedFile = new File(currentDirectory, selectedFile);
                if (clickedFile.isDirectory()) {
                    directoryStack.push(currentDirectory); // push current directory to stack
                    currentDirectory = clickedFile;
                    textLocation.setText(currentDirectory.getPath());
                    listFile();
                } else {
                    Toast.makeText(Activity_1.this, "This is a file", Toast.LENGTH_SHORT).show();
                    // implement logic to play music file
                    Intent intent = new Intent(Activity_1.this, MainActivity2.class);
                    intent.putExtra("filePath", clickedFile.getAbsolutePath());

                    // Get all audio files in the current directory
                    File[] files = currentDirectory.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".mp3") || name.endsWith(".wav");
                        }
                    });

                    // Convert the File array to an ArrayList of file paths
                    ArrayList<String> songFilePaths = new ArrayList<>();
                    for (File file : files) {
                        songFilePaths.add(file.getAbsolutePath());
                    }

                    // Pass the ArrayList of file paths to MainActivity2
                    intent.putStringArrayListExtra("songFilePaths", songFilePaths);

                    startActivity(intent);
                }
                // Update the selected item
                selectedItem = position;
                customArrayAdapter.setSelectedItem(selectedItem);
                customArrayAdapter.notifyDataSetChanged();

                // Change the background color of the selected item to blue
                view.setBackgroundColor(Color.BLUE);
            }
        });


        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decrement the selected item
                selectedItem--;

                // If the selected item is less than 0, set it to the last index
                if (selectedItem < 0) {
                    selectedItem = customArrayAdapter.getCount() - 1;
                }

                // Update the selection in the CustomArrayAdapter
                customArrayAdapter.setSelectedItem(selectedItem);
                customArrayAdapter.notifyDataSetChanged();

            }
        });
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if an item is selected
                if (selectedItem >= 0) {
                    // Programmatically trigger the ListView's onItemClick event
                    listView.performItemClick(
                            listView.getAdapter().getView(selectedItem, null, null),
                            selectedItem,
                            listView.getAdapter().getItemId(selectedItem)
                    );
                } else {
                    Toast.makeText(Activity_1.this, "No item selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (!directoryStack.isEmpty()) {
            currentDirectory = directoryStack.pop(); // pop the last directory from the stack
            textLocation.setText(currentDirectory.getPath());
            listFile();
        } else {
            super.onBackPressed();
        }
    }




    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(Activity_1.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//                        doStuff();
//                        listFile();
                    }
                } else {
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }

    }

    private void listFile() {
        customArrayAdapter.clear();
        if (currentDirectory.isDirectory()) {
            File[] files = currentDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    customArrayAdapter.add(file.getName());
                }
                listView.setAdapter(customArrayAdapter);
            }
        }
        customArrayAdapter.notifyDataSetChanged();
    }
}


