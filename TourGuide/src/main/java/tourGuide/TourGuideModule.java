package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.service.RewardsService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class TourGuideModule {

	@Bean
	public ExecutorService getExecutorService() {
		return Executors.newFixedThreadPool(1000);
	}
	
	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(this.getGpsUtil(), this.getRewardCentral(), this.getExecutorService());
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
}