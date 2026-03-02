package com.harshana.gemstore.service;

import com.harshana.gemstore.entity.Gem;
import com.harshana.gemstore.repository.GemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GemService {

    private final GemRepository gemRepository;

    public List<Gem> getAllGems() {
        return gemRepository.findAll();
    }

    public List<Gem> filterGems(Long categoryId, Double minPrice, Double maxPrice, Double minCarat, Double maxCarat) {
        return gemRepository.filterGems(categoryId, minPrice, maxPrice, minCarat, maxCarat);
    }

    public Gem saveGem(Gem gem) {
        return gemRepository.save(gem);
    }

    public void deleteGem(Long id) {
        gemRepository.deleteById(id);
    }

    public Gem getGemById(Long id) {
        return gemRepository.findById(id).orElse(null);
    }
}
