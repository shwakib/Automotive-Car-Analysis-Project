package carpriceanalysis;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencsv.CSVWriter;

public class project {
	private static final List<String[]> carDataRows = new ArrayList<>();
	
//	private static final WebDriver driver=new FirefoxDriver();
	private static final WebDriver driver=new ChromeDriver();
	
	public static void websitesToCrawl() throws InterruptedException {
		Scanner userInput = new Scanner(System.in);

        System.out.println("Do you want to crawl any specific website?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.print("Enter your choice: ");

        int userChoice = userInput.nextInt();

        switch (userChoice) {
            case 1:
                System.out.println("Enter the website URL:");
                userInput.nextLine();
                String websiteUrl = userInput.nextLine();

                if (isValidWebsite(websiteUrl)) {
                    crawlWebsite(websiteUrl);
                } else {
                    System.out.println("Invalid website URL. Please enter a valid URL.");
                }
                break;

            case 2:
                crawlDefaultWebsite();
                break;

            default:
                System.out.println("Invalid choice. Please choose 1 or 2.");
        }

        userInput.close();
	}
	
	public static boolean isValidWebsite(String websiteUrl) {
		String regex = "^(https?|ftp):\\/\\/www\\.[\\w\\d\\-]+(\\.[\\w\\d\\-]+)+([\\w\\d\\-.,@?^=%&:/~+#]*[\\w\\d\\-@?^=%&/~+#])?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(websiteUrl);

        return matcher.matches();
    }

    private static void crawlWebsite(String websiteUrl) throws InterruptedException {
        System.out.println("Crawling website: " + websiteUrl);
        driver.get(websiteUrl);
        Thread.sleep(5000);
    }

    private static void crawlDefaultWebsite() throws InterruptedException {
        System.out.println("Crawling selected websites.");
        
		crawlCarGurus();
		
		Thread.sleep(2000);
    }
	
	public static void crawlCarDoor() throws InterruptedException{
		driver.get("https://www.cardoor.ca/");
		driver.findElement(By.xpath("//button[normalize-space()='Buy a car']")).click();
	}
	
	public static void crawlCarGurus() throws InterruptedException {
		int currentPage=0;
		int totalPages=0;
		String phoneNumber;
		driver.get("https://www.cargurus.ca/");
//		String URL=driver.getCurrentUrl();
//		int indexOfURL=driver.getCurrentUrl().indexOf("www.");
//		String domain=URL.substring(indexOfURL + 4);
//		System.out.println(domain);
		String domain=extractDomain(driver.getCurrentUrl());
		driver.findElement(By.xpath("//span[normalize-space()='Buy']")).click();
		driver.findElement(By.xpath("//a[@alt='Sedan Body Style']//div[@class='card noBorder center bodyoption clickable']")).click();
		String pages=driver.findElement(By.xpath("//span[@class='_A3m0_']")).getText();
		System.out.println(pages);
		int indexOfPage = pages.indexOf("Page ");
		int indexOfOf = pages.indexOf(" of ");
		if (indexOfPage != -1 && indexOfOf != -1) {
		    currentPage = Integer.parseInt(pages.substring(indexOfPage + 5, indexOfOf).trim());
		    totalPages = Integer.parseInt(pages.substring(indexOfOf + 4).trim());
		    System.out.println("Current Page: " + currentPage);
		    System.out.println("Total Pages: " + totalPages);
		}
		for(int i=currentPage;i<=15;i++) {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			driver.findElement(By.xpath("//button[@aria-labelledby='bottomPaginationNext']//*[name()='svg']")).click();
			List<WebElement> carsintheSite=driver.findElements(By.xpath("//div[@class='k4FSCT']"));
			int pgSize=carsintheSite.size();
//			System.out.println(pgSize);
			for(int j=1;j<=pgSize;j++) {
				try {
					String carName = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//div[@class='k4FSCT']//div//h4[@class='gN7yGT'])[" + j + "]"))).getText();
                    String carPrice =driver.findElement(By.xpath("(//div[@class='k4FSCT']//div[@class='YlkCzK']//div[@class='Lxkk9T']//h4[@class='ulx4Y8'])["+j+"]")).getText();
                    String carMileage=driver.findElement(By.xpath("(//div[@class='k4FSCT']//div[@class='YlkCzK']//div[@class='EeLi0s']//p[@class='HczmlC'])["+j+"]")).getText();
                    String monthlyPayment=driver.findElement(By.xpath("(//div[@class='k4FSCT']//div[@class='YlkCzK']//div[@class='Lxkk9T']//button)["+j+"]")).getText();
//                    System.out.println("Car Price: " + monthlyPayment);
                    String carLocation=driver.findElement(By.xpath("(//div[@class='k4FSCT']//div[@class='MOU7hw']//div[@class='caX8XQ']//div//p[@class='HLgC_C'])["+j+"]")).getText();
                    
                    String phoneXPath = "(//div[@class='k4FSCT']//div[@class='q7Zi1K Qc4mOg']//button[@class='HaLmAx _9laKps NfgtRG SUJbPV'])[" + j + "]";
                   List<WebElement> phoneElements = driver.findElements(By.xpath(phoneXPath));
                   if (!phoneElements.isEmpty()) {
                       phoneNumber = phoneElements.get(0).getText();
                    } else {
                        phoneNumber = "No Information Found";
                    }
                    
                    List<WebElement> carEngineElements = driver.findElements(By.xpath("(//div[@class='k4FSCT']//div[@class='EeLi0s']//p[@class='coushe'])[" + j + "]"));
                    String carEngine;
                    if (!carEngineElements.isEmpty()) {
                        carEngine = carEngineElements.get(0).getText();
                    } else {
                        carEngine = "No Information Found";
                    }
                    
                    Thread.sleep(2000);
                    String[] carDataRow = {carName, carPrice, carMileage, monthlyPayment, carLocation, phoneNumber, carEngine};
                    carDataRows.add(carDataRow);
                    
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    j--;
                } 
			}
			Thread.sleep(1000);
			System.out.println("The number is :"+i);
		}
		storeCarDataToCSV(domain);
	}
	
	public static void crawlAutoTrader() {
		driver.get("https://www.autotrader.ca/");
		driver.findElement(By.xpath("//a[@id='buyOnlineURL']")).click();
	}
	
	public static void crawlGoAuto() throws InterruptedException {
		driver.get("https://www.goauto.ca/");
		String domain=extractDomain(driver.getCurrentUrl());
//		int indexOfURL=driver.getCurrentUrl().indexOf("www.");
//		String domain=URL.substring(indexOfURL + 4);
//		System.out.println(domain);
		driver.findElement(By.xpath("//nav[@class='header_mainNav__GaPyH hidden md:flex gap-24 items-center']//a[@data-nav='headerMainNavigation'][normalize-space()='Shop']")).click();
		Thread.sleep(3000);
		int currentPage=Integer.parseInt(driver.findElement(By.xpath("//nav[@class='pagination_pagination__2yOxT']//ul//li[@class='pagination_current__RQBwN']")).getText());
//		System.out.println(currentPage);
		List<WebElement> totalPage=driver.findElements(By.xpath("//nav[@class='pagination_pagination__2yOxT']//ul//li[@class='pagination_pageNum__4PcSb']"));
		int lastElement = Integer.parseInt(totalPage.get(totalPage.size() - 1).getText());
//		System.out.println(lastElement);
		
		for (int i = currentPage; i <= 15; i++) {
//			driver.findElement(By.xpath("//nav[@class='pagination_pagination__2yOxT']//div[@class='pagination_next__UQxA3']//button[@class='button_root__ebVgz button_contextLight__2lZAC button_outline__aOaXB button_small__KgoXT typ-button-small button_widthAuto__PPtZs button_secondary__CsBrW']")).click();
//			Thread.sleep(2000);
			List<WebElement> numberofCars=driver.findElements(By.xpath("//div[@class='grid gap-24']//div[@class='grid-cols-4 mb-64']//div[@class='inventory_inventoryListing__vHmrR']//div[@class='background-hint_light__EI87j bg-white text-gray-700 inventory_inventoryCard__XCsAr typ-body-3 undefined inventory_isLinked__frz0l']"));
			int carsInThePage=numberofCars.size();
//			System.out.println(carsInThePage);
				for(int j=1;j<=carsInThePage;j++) {
					try {
						WebElement pagei=driver.findElement(By.xpath("(//div[@class='grid gap-24']//div[@class='grid-cols-4 mb-64']//div[@class='inventory_inventoryListing__vHmrR']//div[@class='background-hint_light__EI87j bg-white text-gray-700 inventory_inventoryCard__XCsAr typ-body-3 undefined inventory_isLinked__frz0l'])["+j+"]"));
						String mileage=driver.findElement(By.xpath("(//div[@class='inventory_content__DIqP5']//p[@class='inventory_trimMileage__CC0Yp']//span[@class='inventory_mileage__M6cGj'])["+j+"]")).getText();
						String carName=driver.findElement(By.xpath("(//div[@class='inventory_content__DIqP5']//h4[@class='inventory_makeAndModel__jLJyd typ-body-2 !font-bold'])["+j+"]")).getText();
						String price=driver.findElement(By.xpath("(//div[@class='inventory_content__DIqP5']//p[@class='inventory_pricing__GwjgT typ-body-1 !font-bold'])["+j+"]")).getText();
						pagei.click();
//						String make=driver.findElement(By.xpath("//div[@class='vdp-header_wrapper__JuYOS']//div[@class='vdp-header_content__6FRB1']//p[@class='vdp-header_make__BfJnk typ-body-2 md:typ-body-1']")).getText();
//						String finalCarName=make+" "+carName;
						String transmission=driver.findElement(By.xpath("//div[@class='vdp-header_wrapper__JuYOS']//div[@class='vdp-header_content__6FRB1']//p[@class='vdp-header_trim__eQyJ7 typ-headline-7']")).getText();
//						String price=driver.findElement(By.xpath("//div[@class='price-lock-up_priceLockUp__urZa7']//div[@class='price-lock-up_priceDetails__9xBqR']//h3[@class='price-lock-up_pricing__t1ZDN typ-headline-3']")).getText().split("plus")[0].trim();
						String address=driver.findElement(By.xpath("//p[@class='vdp-header_dealership__n6DWR typ-body-2']")).getText();
						String phoneNumber=driver.findElement(By.xpath("//a[@class='styled-link text-gray-700']")).getText();
//						System.out.println(carName);
//						System.out.println(mileage);
//						System.out.println(price);
//						System.out.println(address);
//						System.out.println(phoneNumber);
						Thread.sleep(5000);
						String[] carDataRow = {carName, price, mileage, " ", address, phoneNumber, transmission};
	                    carDataRows.add(carDataRow);
						driver.findElement(By.xpath("//div[@class='flex gap-4 items-center']")).click();					
					}
					catch(StaleElementReferenceException e) {
						j--;
					}
				}
				driver.findElement(By.xpath("//nav[@class='pagination_pagination__2yOxT']//div[@class='pagination_next__UQxA3']//button[@class='button_root__ebVgz button_contextLight__2lZAC button_outline__aOaXB button_small__KgoXT typ-button-small button_widthAuto__PPtZs button_secondary__CsBrW']")).click();
		}
		storeCarDataToCSV(domain);
	}
	
	public static String extractDomain(String url) {
        String domain = "";
        int wwwIndex = url.indexOf("www.");
        if (wwwIndex != -1) {
            domain = url.substring(wwwIndex + 4);
            int slashIndex = domain.indexOf("/");
            if (slashIndex != -1) {
                domain = domain.substring(0, slashIndex);
            }
        }

        return domain;
    }
	
	public static void storeCarDataToCSV(String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName + ".csv", true))) {
            String[] header = {"Car Name", "Car Price", "Car Mileage", "Monthly Payment", "Car Location", "Phone Number", "Car Engine"};
            writer.writeNext(header);

            for (String[] carDataRow : carDataRows) {
                writer.writeNext(carDataRow);
            }

            System.out.println("Car data has been added to the CSV file successfully!");
            carDataRows.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) throws InterruptedException {

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
//		websitesToCrawl();
//		crawlAutoTrader();
		crawlGoAuto();
		
		Thread.sleep(5000);
		driver.quit();
	}

}
