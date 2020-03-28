package com.taxi.be;

import com.taxi.fe.feparsing.ReceivedMap;
import com.taxi.be.parsing.CityMap;
import com.taxi.be.parsing.Wall;
import com.taxi.fe.repository.CheckpointRepository;
import com.taxi.fe.repository.MapRepository;
import com.taxi.fe.repository.WallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonValidator {

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private WallRepository wallRepository;

    @Autowired
    private CheckpointRepository checkpointRepository;

    public void storeCityMap(ReceivedMap receivedMap) {
        CityMap newMap = receivedMap.getCityMap();
        if(mapRepository.existsById(newMap.getCityId())) {
            newMap.getWalls().clear();
            mapRepository.delete(newMap);
        }
        mapRepository.save(newMap);
        for(Wall wall: receivedMap.getWalls()) {
            newMap.addWall(wall);
            wallRepository.save(wall);
        }
//        for (Wall wall : receivedMap.getWalls())
 //           wallRepository.save(wall);
    /*    if (mapRepository.existsById(newMap.getId())) {
            mapRepository.delete(newMap);
        }

        mapRepository.save(receivedMap.getCityMap());
        for (Wall wall : receivedMap.getWalls())
            wallRepository.save(wall);*/
     //   for (Checkpoint checkpoint : receivedMap.getCheckpoints())
      //      checkpointRepository.save(checkpoint);
    }


}

