package com.shuowen.yuzong.service.impl;

import com.shuowen.yuzong.dao.mapper.StudentMapper;
import com.shuowen.yuzong.dao.model.Student;
import com.shuowen.yuzong.service.Interface.StudentService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public boolean addStudent(Student student) {
        if (student == null){
            return false;
        }
        if (StringUtils.isEmpty(student.getName())){
            return false;
        }
        return studentMapper.insertStudent(student) == 1;
    }

    @Override
    public Student findStudentByName(String name) {
        if (StringUtils.isEmpty(name)){
            return null;
        }

        return studentMapper.findByName(name);


    }
}
