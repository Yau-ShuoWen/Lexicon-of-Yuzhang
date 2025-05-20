package com.shuowen.yuzong.controller;

import com.shuowen.yuzong.dao.model.Student;
import com.shuowen.yuzong.service.Interface.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestMysqlController {
    @Autowired
    private StudentService studentService;

    @RequestMapping(value = "/api/student/add",method = RequestMethod.POST)
    public Object addStudent(@RequestBody Student student){

        return studentService.addStudent(student);
    }

    @RequestMapping("/api/student/get")
    public Object findByName(@RequestParam String name){
        return studentService.findStudentByName(name);
    }
}
