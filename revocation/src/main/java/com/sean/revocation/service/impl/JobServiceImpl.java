/**
 * 
 */
package com.sean.revocation.service.impl;

import com.sean.revocation.repository.Job;
import com.sean.revocation.service.JobService;

/**
 * @author zfk_0
 *
 */
public class JobServiceImpl implements JobService {
	public String saveNewJob(Job job) {
		return job.getName();
	}

	public String findJob() {
		return "OK";
	}
}
