package com.library.music;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:mysql://localhost:3306/springboot_library","spring.datasource.username=springbootuser","spring.datasource.password=mysqlspringbootpassword"})
class LibraryApplicationTests {

	@Test
	void contextLoads() {
	}

}
