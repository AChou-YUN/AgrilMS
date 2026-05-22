package org.example.demo222.service;

import org.example.demo222.entity.Unit;
import java.util.List;

public interface UnitService {
    List<Unit> getAllUnits();
    void createUnit(Unit unit);
    void deleteUnit(Long id);
}