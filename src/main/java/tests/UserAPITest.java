package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UserAPITest extends BaseTest {

    @DataProvider(name = "userDataProvider")
    public Object[][] userDataProvider() {
        return new Object[][] {
                {"John Doe"},
        };
    }

    @Test
    public void getUserDetails() {
        ExtentTest test = extent.createTest("testGetUserDetails", "Verify user details API");
        testThreadLocal.set(test);

        Response response = sendGetRequest("/users/me");

        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 200");
        Assert.assertNotNull(response.jsonPath().getString("name"), "User name should not be null");
    }

    @Test(dataProvider = "userDataProvider")
    public void createNewPost(String description) {
        ExtentTest test = extent.createTest("createNewPost", "Create New Post");
        testThreadLocal.set(test);

        String requestBody = String.format("{\"description\": \"%s\"}", description);
        Response response = sendPostRequest("/posts", requestBody);

        Assert.assertEquals(response.getStatusCode(), 201, "Expected status code is 201");
        Assert.assertEquals(response.jsonPath().getString("_id"), "Mike", "User ID should not be null");
    }
}