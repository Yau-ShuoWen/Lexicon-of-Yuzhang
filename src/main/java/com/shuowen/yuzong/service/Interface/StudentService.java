package com.shuowen.yuzong.service.Interface;

import com.shuowen.yuzong.dao.model.Student;

public interface StudentService {

     boolean addStudent(Student student);

     Student findStudentByName(String name);

}
