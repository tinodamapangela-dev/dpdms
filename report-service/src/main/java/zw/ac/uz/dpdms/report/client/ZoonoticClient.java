package zw.ac.uz.dpdms.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "zoonotic-disease-service")
public interface ZoonoticClient { @GetMapping("/api/zoonotic") List<Map<String,Object>> list(); }