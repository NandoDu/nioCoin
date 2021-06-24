package niocoin.kgsystem.kgbackend.controller;

import niocoin.kgsystem.kgbackend.dto.modifyNodeDTO;
import niocoin.kgsystem.kgbackend.dto.modifyRelationDTO;
import niocoin.kgsystem.kgbackend.dto.nodeDTO;
import niocoin.kgsystem.kgbackend.dto.relationDTO;
import niocoin.kgsystem.kgbackend.enums.GraphStatus;
import niocoin.kgsystem.kgbackend.service.GraphService;
import niocoin.kgsystem.kgbackend.service.ModifyService;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Res;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/modify")
public class ModifyController {
    private final ModifyService modifyService;
    private final GraphService graphService;

    public ModifyController(ModifyService modifyService, GraphService graphService) {
        this.modifyService = modifyService;
        this.graphService = graphService;
    }


    @PostMapping("/node")
    public Res modifyNode(@RequestBody nodeDTO node) throws Err {
        return Res.ok(modifyService.modifyNode(node));
    }

    @PostMapping("/modifyNodes")
    public Res modifyNodes (@RequestBody modifyNodeDTO modifyNodeDTO) throws Err {
        graphService.setGraphStatus(modifyNodeDTO.getGraphId(),GraphStatus.UserModify);
        return Res.ok(modifyService.modifyNodes(modifyNodeDTO));
    }

    @PostMapping("/relation")
    public Res modifyRelation(@RequestBody relationDTO relation) throws Err{
        return Res.ok(modifyService.modifyRelation(relation));
    }

    @PostMapping("/modifyRelations")
    public Res modifyRelations (@RequestBody modifyRelationDTO modifyRelationDTO) throws Err {
        return Res.ok(modifyService.modifyRelations(modifyRelationDTO));
    }
}
