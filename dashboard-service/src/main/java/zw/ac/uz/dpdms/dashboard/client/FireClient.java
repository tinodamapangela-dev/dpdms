package zw.ac.uz.dpdms.dashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "fire-service")
public interface FireClient { @GetMapping("/api/fires") List<Map<String,Object>> list(); }