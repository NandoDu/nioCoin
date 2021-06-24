package niocoin.kgsystem.kgbackend.controller;

import niocoin.kgsystem.kgbackend.dto.nreDTO;
import niocoin.kgsystem.kgbackend.enums.GraphStatus;
import niocoin.kgsystem.kgbackend.service.GraphService;
import niocoin.kgsystem.kgbackend.service.NREService;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import niocoin.kgsystem.kgbackend.util.Res;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/nre")
public class NREController {

    private final NREService NREService;
    private final GraphService graphService;

    public NREController(NREService NREService, GraphService graphService) {
        this.NREService = NREService;
        this.graphService = graphService;
    }

    /**
     * 第一次NRE识别请求的接口
     *
     * @param nreDTO 需要识别的文本DTO
     * @return 返回空为申请已经发送，否则抛出ERR类直接触发前端自动错误提示
     */
    @PostMapping("/first")
    public Res nreText(@RequestBody nreDTO nreDTO) throws Err {
        Neo4jUtil.clearCurrent(nreDTO.getGraphId());
        graphService.setGraphStatus(nreDTO.getGraphId(), GraphStatus.FirstNRESent);
        NREService.pythonNRE(nreDTO);
        return Res.ok();
    }

    /**
     * 第二次NRE的识别请求接口
     *
     * @param @param nreDTO 需要识别的文本DTO
     * @return 返回空为申请已经发送，否则抛出ERR类直接触发前端自动错误提示
     */
    @PostMapping("/second")
    public Res nreSecond(@RequestBody nreDTO nreDTO) throws Err {
        Neo4jUtil.clearCurrent(nreDTO.getGraphId());
        graphService.setGraphStatus(nreDTO.getGraphId(), GraphStatus.SecondNRESent);
        NREService.pythonNRE(nreDTO);
        return Res.ok();
    }

    /**
     * 这个方法返回的是GraphStatus枚举类中的几个状态，可以看代码，在enums文件夹中
     *
     * @param graphId 图谱ID
     * @return 返回图谱状态
     */
    @GetMapping("/status")
    public Res getGraphStatus(@RequestParam long graphId) {
        return Res.ok(graphService.getGraphStatus(graphId));
    }

}
