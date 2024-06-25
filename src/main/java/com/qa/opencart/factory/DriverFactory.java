package com.qa.opencart.factory;

import static org.testng.Assert.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.qa.opencart.exceptions.FrameWorkException;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.bytebuddy.asm.Advice.Return;

public class DriverFactory {

	/**
	 * Rakesh is creating the framework (POM).
	 */

	WebDriver driver;
	Properties prop;
	OptionsManager optionsManager;
	public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();

	/**
	 * This method initializes the WebDriver based on the given browser.
	 * 
	 * @param browserName the name of the browser to initialize the WebDriver for
	 * @return the initialized WebDriver
	 */

	public WebDriver init_driver(Properties prop) {

		String browserName = prop.getProperty("browser").trim();

		optionsManager = new OptionsManager(prop);

		System.out.println(" Browser name is :" + browserName);

		if (browserName.equalsIgnoreCase("chrome")) {
			WebDriverManager.chromedriver().setup();
			// driver = new ChromeDriver(optionsManager.getChromeOptions());
			tlDriver.set(new ChromeDriver(optionsManager.getChromeOptions()));

		} else if (browserName.equalsIgnoreCase("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			// driver = new FirefoxDriver(optionsManager.getFirefoxOptions());
			tlDriver.set(new FirefoxDriver(optionsManager.getFirefoxOptions()));

		} else if (browserName.equalsIgnoreCase("edge")) {
			WebDriverManager.edgedriver().setup();
			// driver = new EdgeDriver(optionsManager.getEdgeOptions());
			tlDriver.set(new EdgeDriver(optionsManager.getEdgeOptions()));

		} else {
			System.out.println("Please enter the right browser :" + browserName);
		}
		getDriver().manage().deleteAllCookies();
		getDriver().manage().window().maximize();
		getDriver().get(prop.getProperty("url"));
		return getDriver();

	}

	public synchronized WebDriver getDriver() {
		return tlDriver.get();

	}

	public Properties init_prop() {

		FileInputStream ip = null;
		prop = new Properties();

		/**
		 * mven command line argument mvn claen install -Denv
		 * 
		 */
		String envName = System.getProperty("env");
		System.out.println("Running tests on environement: " + envName);

		if (envName == null) {
			System.out.println("No env is given....hence running in QA");
			try {
				ip = new FileInputStream(".\\src\\test\\resources\\config\\config.properties");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			try {
				switch (envName.toLowerCase()) {
				case "prod":
					ip = new FileInputStream(".\\src\\test\\resources\\config\\qa.config.properties");
					break;

				case "qa":
					ip = new FileInputStream(".\\src\\test\\resources\\config\\qa.config.properties");
					break;

				case "dev":
					ip = new FileInputStream(".\\src\\test\\resources\\config\\dev.config.properties");
					break;

				case "stage":
					ip = new FileInputStream(".\\src\\test\\resources\\config\\stage.config.properties");
					break;

				case "uat":
					ip = new FileInputStream(".\\src\\test\\resources\\config\\uat.config.properties");
					break;

				default:
					System.out.println("please pass the right environment values ...." + envName);
					throw new FrameWorkException("No env found ..");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (FrameWorkException e) {
				e.printStackTrace();
			}
		}
		try {
			prop.load(ip);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prop;
	}

	/**
	 * take screenshot
	 * 
	 * @return
	 */
	public String getScreenshot() {
		File srcFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		String path = "./screenshot/" + System.currentTimeMillis() + ".png";
		File destination = new File(path);
		try {
			FileUtils.copyFile(srcFile, destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;

	}

}
