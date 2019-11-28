package revocation;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import com.sean.revocation.proxy.CustomInvocationProxy;
import com.sean.revocation.proxy.CustomProxy;
import com.sean.revocation.proxy.InvocationHandlerImpl;
import com.sean.revocation.proxy.ProxyService;
import com.sean.revocation.repository.Job;
import com.sean.revocation.service.JobService;
import com.sean.revocation.service.impl.JobServiceImpl;

public class JobServiceTest {
	private Job job = new Job();
	
	@Before
	public void setUp() {
		job.setName("job");
	}
	
	@Test
	public void test() {
		JobService jobService = ProxyService.getJobService();
		assertArrayEquals(new String[] {"OK"}, new String[] {jobService.findJob()});
		assertArrayEquals(new String[] {"job"}, new String[] {jobService.saveNewJob(job)});
	}
	
	@Test
	public void testCustomerProxy() {
		JobService jobService = CustomProxy.getJobService(JobServiceImpl.class);
		assertArrayEquals(new String[] {"OK"}, new String[] {jobService.findJob()});
		assertArrayEquals(new String[] {"job"}, new String[] {jobService.saveNewJob(job)});
	}
	
	@Test
	public void testCustomInvocationProxy() {
		JobService jobService = CustomInvocationProxy.getJobService(new InvocationHandlerImpl(new JobServiceImpl()));
		assertArrayEquals(new String[] {"OK"}, new String[] {jobService.findJob()});
		assertArrayEquals(new String[] {"job"}, new String[] {jobService.saveNewJob(job)});
	}
}
