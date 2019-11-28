package com.sean.revocation.service;

import com.sean.revocation.repository.Job;

public interface JobService {
	public String saveNewJob(Job job);
	
	public String findJob();
}
