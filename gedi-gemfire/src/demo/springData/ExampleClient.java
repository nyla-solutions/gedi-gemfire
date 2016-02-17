package solutions.gedi.demo.springData;

import java.math.BigDecimal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ExampleClient
{
	
	ApplicationContext context = new ClassPathXmlApplicationContext("client/cache-config.xml");
	
	CalculateTotalSalesForProductInvoker calculateTotalForProduct = context.getBean(CalculateTotalSalesForProductInvoker.class);
	
	String[] products = new String[]{"Apple iPad","Apple iPod","Apple macBook"};
	
	for (String productName: products){
		BigDecimal total = calculateTotalForProduct.forProduct(productName);
		log.info("total sales for " + productName +  " = $" + total);
}
