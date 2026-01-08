package com.example.studentmanagementapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studentmanagementapp.ui.theme.StudentManagementAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentManagementAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudentApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun StudentApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var studentList by remember { mutableStateOf(dbHelper.getAllStudents()) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = course,
            onValueChange = { course = it },
            label = { Text("Course") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (name.isNotEmpty() && email.isNotEmpty() && course.isNotEmpty()) {
                    val result = dbHelper.addStudent(Student(name = name, email = email, course = course))
                    if (result != -1L) {
                        Toast.makeText(context, "Student Added", Toast.LENGTH_SHORT).show()
                        studentList = dbHelper.getAllStudents()
                        name = ""; email = ""; course = ""
                    }
                }
            }) {
                Text("Add")
            }

            Button(onClick = {
                selectedStudent?.let {
                    val updatedStudent = it.copy(name = name, email = email, course = course)
                    val result = dbHelper.updateStudent(updatedStudent)
                    if (result > 0) {
                        Toast.makeText(context, "Student Updated", Toast.LENGTH_SHORT).show()
                        studentList = dbHelper.getAllStudents()
                        selectedStudent = null
                        name = ""; email = ""; course = ""
                    }
                }
            }, enabled = selectedStudent != null) {
                Text("Update")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(studentList) { student ->
                StudentItem(
                    student = student,
                    onEdit = {
                        selectedStudent = student
                        name = student.name
                        email = student.email
                        course = student.course
                    },
                    onDelete = {
                        dbHelper.deleteStudent(student.id)
                        studentList = dbHelper.getAllStudents()
                        Toast.makeText(context, "Student Deleted", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun StudentItem(student: Student, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, style = MaterialTheme.typography.titleMedium)
                Text(text = student.email, style = MaterialTheme.typography.bodySmall)
                Text(text = student.course, style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
