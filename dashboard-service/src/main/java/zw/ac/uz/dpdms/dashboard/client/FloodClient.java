package zw.ac.uz.dpdms.dashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "flood-service")
public interface FloodClient { @GetMapping("/api/floods") List<Map<String,Object>> list(); }