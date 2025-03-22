package com.sl.sdn.repository;

import com.sl.sdn.dto.TransportLineNodeDTO;
import com.sl.sdn.node.AgencyEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import javax.annotation.Resource;
@SpringBootTest
class AgencyRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(AgencyRepositoryTest.class);
    @Resource
    private AgencyRepository agencyRepository;
    @Resource
    private TransportLineRepository transportLineRepository;
    @BeforeEach
    void setUp() {
        System.out.println("方法前");
    }

    @AfterEach
    void tearDown() {
        System.out.println("方法后");
    }

    @Test
    void findByBid() {
        AgencyEntity byBid = agencyRepository.findByBid(1567l);
        System.out.println(byBid);
    }

    @Test
    void findAll() {
        PageRequest bid = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "bid"));
        Page<AgencyEntity> all = agencyRepository.findAll(bid);
        all.forEach(System.out::println);
    }

    @Test
    void deleteByBid() {
        agencyRepository.deleteByBid(1567l);
    }
    @Test
    void saves() {
        AgencyEntity agencyEntity = new AgencyEntity();
        agencyEntity.setBid(1567l);
        agencyEntity.setName("航头营业厅");
        agencyEntity.setPhone("12345678901");
        agencyEntity.setAddress("航都路18号");
        agencyRepository.save(agencyEntity);

    }

    @Test
    void findShortPath() {
        AgencyEntity start = AgencyEntity.builder().bid(100280L).build();
        AgencyEntity end = AgencyEntity.builder().bid(210057L).build();
        TransportLineNodeDTO shortestPath = transportLineRepository.findShortestPath(start, end);
        log.error("最短路径：{}", shortestPath);
        System.out.println(shortestPath);
    }

    @Test
    void findCostPaths() {
        AgencyEntity start = AgencyEntity.builder().name("北京市昌平区定泗路").build();
        AgencyEntity end = AgencyEntity.builder().name("上海市浦东新区南汇").build();
        TransportLineNodeDTO costPath = transportLineRepository.findCostPath(start, end);
        log.error("最少成本路径：{}", costPath);
        System.out.println(costPath);
    }
}