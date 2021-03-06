package com.gamakdragons.wheretruck.domain.favorite.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;

import java.io.IOException;
import java.util.UUID;

import com.gamakdragons.wheretruck.TestIndexUtil;
import com.gamakdragons.wheretruck.common.IndexUpdateResultDto;
import com.gamakdragons.wheretruck.common.SearchResultDto;
import com.gamakdragons.wheretruck.domain.favorite.entity.Favorite;
import com.gamakdragons.wheretruck.domain.truck.entity.Truck;
import com.gamakdragons.wheretruck.test_config.ElasticSearchTestConfig;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {FavoriteServiceImpl.class, ElasticSearchTestConfig.class, TestIndexUtil.class}, 
                properties = {"spring.config.location=classpath:application-test.yml"})
public class FavoriteServiceImplPlatformTest {

	@Autowired
	private FavoriteService favoriteService;

	@BeforeAll
    public static void beforeAll() {
        TestIndexUtil.createElasticSearchTestContainer();
    }

    @AfterAll
    public static void afterAll() {
        TestIndexUtil.closeElasticSearchTestContainer();
    }

	@BeforeEach
	public void setup() throws IOException {

		TestIndexUtil.initRestHighLevelClient();
		TestIndexUtil.deleteTestFavoriteIndex();
		TestIndexUtil.createTestFavoriteIndex();
	}

	@AfterEach
	public void teardown() throws IOException {
		TestIndexUtil.deleteTestFavoriteIndex();
	}

	@Test
	void testDeleteFavorite() {

		Favorite favorite = new Favorite();
		String userId = UUID.randomUUID().toString();
		String truckId = UUID.randomUUID().toString();
		favorite.setUserId(userId);
		favorite.setTruckId(truckId);
		
		IndexUpdateResultDto result = favoriteService.saveFavorite(favorite);
		String id = result.getId();
		assertThat(result.getResult(), is("CREATED"));
		assertThat(result.getId().length(), is(greaterThan(0)));

		try {
			Thread.sleep(2000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		assertThat(favoriteService.deleteFavorite(id).getResult(), is("DELETED"));
	}

	@Test
	void testCountByTruckId() {

		Favorite favorite = new Favorite();
		String userId = UUID.randomUUID().toString();
		String truckId = UUID.randomUUID().toString();
		favorite.setUserId(userId);
		favorite.setTruckId(truckId);
		
		IndexUpdateResultDto result = favoriteService.saveFavorite(favorite);
		assertThat(result.getResult(), is("CREATED"));
		assertThat(result.getId().length(), is(greaterThan(0)));

		try {
			Thread.sleep(2000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		assertThat(favoriteService.countByTruckId(truckId), is(1));
	}

	@Test
	void testFindByUserId() {

		Favorite favorite = new Favorite();
		String userId = UUID.randomUUID().toString();
		String truckId = UUID.randomUUID().toString();
		favorite.setUserId(userId);
		favorite.setTruckId(truckId);
		
		IndexUpdateResultDto result = favoriteService.saveFavorite(favorite);
		assertThat(result.getResult(), is("CREATED"));
		assertThat(result.getId().length(), is(greaterThan(0)));

		try {
			Thread.sleep(2000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		SearchResultDto<Truck> searchResult = favoriteService.findByUserId(userId);
		assertThat(searchResult.getDocs(), hasItem(allOf(hasProperty("id", is(truckId)), hasProperty("userId", is(userId)))));
	}

	@Test
	void testSaveFavorite() {

		Favorite favorite = new Favorite();
		favorite.setUserId(UUID.randomUUID().toString());
		favorite.setTruckId(UUID.randomUUID().toString());
		
		IndexUpdateResultDto result = favoriteService.saveFavorite(favorite);
		assertThat(result.getResult(), is("CREATED"));
		assertThat(result.getId().length(), is(greaterThan(0)));

		try {
			Thread.sleep(2000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
