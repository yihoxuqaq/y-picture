package top.yihoxu.ypicturebackend.manager;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;
import top.yihoxu.ypicturebackend.config.CosClientConfig;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedList;

@Component
public class CosManager {
    @Resource
    private COSClient cosClient;

    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 上传文件
     *
     * @param key  唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key
     * @return
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }


    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        //对图片进行处理（获取基本信息也被视为一种处理）
        PicOperations picOperations = new PicOperations();
        //1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        //添加图片处理规则
        LinkedList<PicOperations.Rule> ruleList = new LinkedList<>();
        PicOperations.Rule rule1 = new PicOperations.Rule();
        rule1.setBucket(cosClientConfig.getBucket());
        rule1.setRule("imageMogr2/format/webp");
        rule1.setFileId(FileUtil.mainName(key) + ".webp");
        //添加图片处理规则 缩略图
        if (file.length()>1024*2){
            PicOperations.Rule rule2 = new PicOperations.Rule();
            rule2.setBucket(cosClientConfig.getBucket());
            rule2.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128));
            String thumbnail = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            rule2.setFileId(thumbnail);
            ruleList.add(rule2);
        }
        ruleList.add(rule1);
        picOperations.setRules(ruleList);
        //构造处理参数
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }
}
