package org.example.demo222.service.impl;

import org.example.demo222.entity.Unit;
import org.example.demo222.mapper.UnitMapper;
import org.example.demo222.service.UnitService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitMapper unitMapper;

    public UnitServiceImpl(UnitMapper unitMapper) {
        this.unitMapper = unitMapper;
    }

    @Override
    public List<Unit> getAllUnits() {
        return unitMapper.selectAll();
    }

    @Override
    public void createUnit(Unit unit) {
        unitMapper.insert(unit);
    }

    @Override
    public void deleteUnit(Long id) {
        unitMapper.deleteById(id);
    }
}