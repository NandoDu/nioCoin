package niocoin.kgsystem.kgbackend.controller;

import niocoin.kgsystem.kgbackend.dto.SaveThumbDTO;
import niocoin.kgsystem.kgbackend.dto.updateGraphDTO;
import niocoin.kgsystem.kgbackend.service.GraphService;
import niocoin.kgsystem.kgbackend.service.JsonTransService;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import niocoin.kgsystem.kgbackend.util.Res;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("api/graph")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    /**
     * 根据kgId返回所有的节点和关系信息
     *
     * @param kgId 图谱ID
     * @return 返回信息
     */
    @GetMapping("/getGraph")
    public Res transJSON(@RequestParam long kgId) {
        JsonTransService jsonTransService = new JsonTransService();
        return Res.ok(jsonTransService.generateJSONByKgId(kgId));
    }

    /**
     * 根据userId和graphID返回当前图谱名
     */
    @GetMapping("/getGraphName")
    public Res getGraphName(@RequestParam long userId, @RequestParam long graphId) throws Err {
        return Res.ok(graphService.getGraphName(userId, graphId));
    }

    /**
     * 更新以及导入图谱时使用的接口，接收json数据格式，对图谱进行更新或者导入
     *
     * @param updateGraphDTO 图谱数据（json格式）
     * @return 成功时会返回成功信息
     * @throws Err 失败抛出的异常
     */
    @PostMapping("/graph")
    public Res updateGraph(@RequestBody updateGraphDTO updateGraphDTO) throws Err {
        Neo4jUtil.clearCurrent(updateGraphDTO.getKgId());
        return Res.ok(graphService.parseJson(updateGraphDTO));
    }

    @PostMapping(value = "thumb")
    public Res setThumb(@RequestPart("data") SaveThumbDTO saveThumbDTO, @RequestPart("image") MultipartFile image) throws Err {
        try (InputStream stream = image.getInputStream()) {
            graphService.setThumbPicture(saveThumbDTO, stream);
        } catch (IOException | Err e) {
            System.out.println("Failed to open image.");
            e.printStackTrace();
            throw new Err("图谱截图保存失败");
        }
        return Res.ok();
    }

}
