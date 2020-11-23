package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/*
 * A note on performance improvements:
 *
 *     The number of users generated for the high volume tests can be easily adjusted via this method:
 *
 *     		InternalTestHelper.setInternalUserNumber(100000);
 *
 *
 *     These tests can be modified to suit new solutions, just as long as the performance metrics
 *     at the end of the tests remains consistent.
 *
 *     These are performance metrics that we are trying to hit:
 *
 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
 *
 *     highVolumeGetRewards: 100,000 users within 20 minutes:
 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
 */
public class TestPerformance {
	private ExecutorService executorService;
	private GpsUtil gpsUtil;
	private RewardsService rewardsService;
	private TourGuideService tourGuideService;

	private void initTest(){
		executorService = Executors.newFixedThreadPool(1000);
		gpsUtil = new GpsUtil();
		rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
		InternalTestHelper.setInternalUserNumber(100000); // Users should be incremented up to 100,000
		tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

	}

	private void undefTest(){
		InternalTestHelper.setInternalUserNumber(0);
		executorService.shutdown();
		executorService = null;
		gpsUtil = null;
		rewardsService = null;
		tourGuideService = null;
	}

	// Must finish less than 15 minutes
	@Test
	public void highVolumeTrackLocation() throws ExecutionException, InterruptedException {
		initTest();

		List<User> allUsers = tourGuideService.getAllUsers();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		List<VisitedLocation> locationList = tourGuideService.getLocationsFromUserList(allUsers).get();
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();
		assertTrue(locationList.size() > 0);

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

		undefTest();
	}

	// Must finish less than 20 minutes
	@Test
	public void highVolumeGetRewards() throws ExecutionException, InterruptedException {
		initTest();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = tourGuideService.getAllUsers();
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

		rewardsService.calculateUsersListReward(allUsers).get();

		for (User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

		undefTest();
	}
}