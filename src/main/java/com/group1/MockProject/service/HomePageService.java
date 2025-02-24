package com.group1.MockProject.service;

import com.group1.MockProject.dto.response.GuestHomePageDTO;
import com.group1.MockProject.dto.response.StudentHomePageDTO;

public interface HomePageService {
    StudentHomePageDTO getHomePageForStudent(String email);
    GuestHomePageDTO getHomePageForGuest();
}
