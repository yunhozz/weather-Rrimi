package com.batchserver.application;

import com.batchserver.RegionRepository;
import com.batchserver.common.exception.RegionInitFailException;
import com.batchserver.domain.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitRegionService {

    private final RegionService regionService;

//    @PostConstruct
    public void init() {
        regionService.initRegions();
    }


    @Component
    @RequiredArgsConstructor
    public static class RegionService {

        private final RegionRepository regionRepository;

        @Value("${app.file.directory}")
        private String fileDir;

        @Transactional
        public void initRegions() {
            Path path = Paths.get(fileDir + "region_list.csv");
            URI uri = path.toUri();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new UrlResource(uri).getInputStream()))) {
                List<Region> regions = new ArrayList<>();
                String line = br.readLine(); // 첫째 줄 제외

                while ((line = br.readLine()) != null) {
                    String[] split = line.split(",");
                    Region region = Region.builder()
                            .parentRegion(split[0])
                            .childRegion(split[1])
                            .nx(Integer.parseInt(split[2]))
                            .ny(Integer.parseInt(split[3]))
                            .build();
                    regions.add(region);
                }

                regionRepository.saveAll(regions);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RegionInitFailException();
            }
        }
    }
}