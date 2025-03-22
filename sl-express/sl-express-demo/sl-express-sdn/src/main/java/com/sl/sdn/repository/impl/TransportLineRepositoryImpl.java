package com.sl.sdn.repository.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.sl.sdn.dto.OrganDTO;
import com.sl.sdn.dto.TransportLineNodeDTO;
import com.sl.sdn.enums.OrganTypeEnum;
import com.sl.sdn.node.AgencyEntity;
import com.sl.sdn.repository.TransportLineRepository;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class TransportLineRepositoryImpl implements TransportLineRepository {

    @Resource
    private Neo4jClient neo4jClient;

    @Override
    public TransportLineNodeDTO findShortestPath(AgencyEntity start, AgencyEntity end) {
        //获取网点数据在Neo4j中的类型
        String type = start.getClass().getAnnotation(Node.class).value()[0];
        //构造查询语句
        String cypherQuery = StrUtil.format(
                "MATCH path = shortestPath((n:{}) -[*..10]-> (m:{}))\n" +
                        " WHERE n.bid = $startId AND m.bid = $endId " +
                        " RETURN path", type, type);
        //执行cypherQL
        Optional<TransportLineNodeDTO> optional = neo4jClient.query(cypherQuery)
                .bind(start.getBid()).to("startId")
                .bind(end.getBid()).to("endId")
                .fetchAs(TransportLineNodeDTO.class)//封装给TransportLineNodeDTO
                .mappedBy((t, r) -> {
                    Path path = r.get(0).asPath();
                    TransportLineNodeDTO transportLineNodeDTO = new TransportLineNodeDTO();
                    List<OrganDTO> organDTOS = new ArrayList<>();
                    path.nodes().forEach(node -> {
                        //存储所有属性 key  value
                        Map<String, Object> map = node.asMap();
                        //将node的属性map 转为OrganDTO对象
                        OrganDTO organDTO = BeanUtil.toBeanIgnoreError(map, OrganDTO.class);
                        //获取节点所对应的标签类型
                        String typeName = CollUtil.getFirst(node.labels());

                        OrganTypeEnum value = OrganTypeEnum.valueOf(typeName);
                        organDTO.setType(value.getCode());
                        //设置经纬度
                        organDTO.setLongitude(BeanUtil.getProperty(map.get("location"), "x"));
                        organDTO.setLatitude(BeanUtil.getProperty(map.get("location"), "y"));
                        organDTOS.add(organDTO);
                    });
                    transportLineNodeDTO.setNodeList(organDTOS);

                    for (Relationship relationship : path.relationships()) {
                        Map<String, Object> map = relationship.asMap();
                        transportLineNodeDTO.setCost(Convert.toDouble(map.get("cost")) + transportLineNodeDTO.getCost());
                    }
                    return transportLineNodeDTO;
                }).one();

        if (optional.isPresent()) {
            return optional.get();
        } else {
            log.error("未查到路线");
            return null;
        }
    }

    @Override
    public TransportLineNodeDTO findCostPath(AgencyEntity start, AgencyEntity end) {
        //获取网点数据在Neo4j中的类型
        String type = start.getClass().getAnnotation(Node.class).value()[0];
        //构造查询语句
        //执行cypherQL
        String cypherQuery = StrUtil.format(
                "MATCH path = (n:{}) -[*..10]->(m:{})\n" +
                        " WHERE n.name = $local1 AND m.name = $local2\n" +
                        " UNWIND relationships(path) AS r\n" +
                        " WITH sum(r.cost) AS cost, path\n" +
                        " RETURN path ORDER BY cost ASC, LENGTH(path) ASC LIMIT 1", type, type);
        Optional<TransportLineNodeDTO> optional = neo4jClient.query(cypherQuery)
                .bind(start.getName()).to("local1")
                .bind(end.getName()).to("local2")
                .fetchAs(TransportLineNodeDTO.class)
                .mappedBy((t, r) -> {
                    Path path = r.get(0).asPath();
                    TransportLineNodeDTO transportLineNodeDTO = new TransportLineNodeDTO();
                    List<OrganDTO> organDTOS = new ArrayList<>();
                    path.nodes().forEach(node -> {
                        //存储所有属性 key  value
                        Map<String, Object> map = node.asMap();
                        //将node的属性map 转为OrganDTO对象
                        OrganDTO organDTO = BeanUtil.toBeanIgnoreError(map, OrganDTO.class);
                        //获取节点所对应的标签类型
                        String typeName = CollUtil.getFirst(node.labels());

                        OrganTypeEnum value = OrganTypeEnum.valueOf(typeName);
                        organDTO.setType(value.getCode());
                        //设置经纬度
                        organDTO.setLongitude(BeanUtil.getProperty(map.get("location"), "x"));
                        organDTO.setLatitude(BeanUtil.getProperty(map.get("location"), "y"));
                        organDTOS.add(organDTO);
                    });
                    transportLineNodeDTO.setNodeList(organDTOS);

                    for (Relationship relationship : path.relationships()) {
                        Map<String, Object> map = relationship.asMap();
                        transportLineNodeDTO.setCost(Convert.toDouble(map.get("cost")) + transportLineNodeDTO.getCost());
                    }
                    return transportLineNodeDTO;
                })
                .one();

        if (optional.isPresent()) {
            return optional.get();
        } else {
            log.error("未查到成本最低路线");
            return null;
        }
    }
}
