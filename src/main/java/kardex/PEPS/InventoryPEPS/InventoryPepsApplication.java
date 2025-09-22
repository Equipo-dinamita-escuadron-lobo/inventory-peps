package kardex.PEPS.InventoryPEPS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryPepsApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryPepsApplication.class, args);
	}

}
