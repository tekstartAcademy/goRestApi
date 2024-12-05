package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import config.Config;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    protected static String token;
    protected ExtentReports extent;
    protected static ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    @AfterMethod
    public void tearDown(ITestResult result) {
        ExtentTest test = testThreadLocal.get();
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable().getMessage());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "Test Passed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Test Skipped: " + result.getThrowable().getMessage());
        }
    }

    @BeforeSuite
    public void setupReporting() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("Spark.html");
        reporter.config().setDocumentTitle("Automation Report");
        reporter.config().setReportName("API Test Report");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    @AfterSuite
    public void tearDownReporting() {
        extent.flush();
    }

    @BeforeClass
    public void setup() {
        RestAssured.filters(new ResponseLoggingFilter(), new RequestLoggingFilter());
        RestAssured.baseURI = Config.getBaseUrl();
        token = generateAuthToken();
    }

    // Method to generate an auth token
    protected String generateAuthToken() {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", Config.getClientId());
        payload.put("password", Config.getClientSecret());

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(payload)
                .post(Config.getAuthEndpoint());

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getString("token");
        } else {
            System.out.printf("status code: %s", response.getStatusCode());
            throw new RuntimeException("Failed with response: " + response.jsonPath().get());
        }
    }

    // Utility method to set default headers
    protected Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }

    // Utility method to send a GET request with retry logic
    protected Response sendGetRequest(String endpoint) {
            try {
                return RestAssured.given().headers(getDefaultHeaders()).get(endpoint);
            } catch (Exception e) {
                    throw new RuntimeException("GET request failed", e);
        }
    }

    // Utility method to send a POST request with retry logic
    protected Response sendPostRequest(String endpoint, String body) {
            try {
                return RestAssured.given().headers(getDefaultHeaders()).body(body).post(endpoint);
            } catch (Exception e) {
                    throw new RuntimeException("POST request failed after multiple retries", e);
        }
    }
}
