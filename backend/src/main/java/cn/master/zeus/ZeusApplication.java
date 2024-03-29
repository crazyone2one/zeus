package cn.master.zeus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 11'papa
 */
@SpringBootApplication
//@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@MapperScan("cn.master.zeus.mapper")
public class ZeusApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeusApplication.class, args);
	}

}
