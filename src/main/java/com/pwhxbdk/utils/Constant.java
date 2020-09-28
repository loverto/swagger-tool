package com.pwhxbdk.utils;

/**
 * @author ylf
 * @version 1.0.0
 * @description 常量类
 * @date 2020/9/27 9:22
 **/
public interface Constant {
    String MAPPING_VALUE = "value";
    String MAPPING_METHOD = "method";

    String CONTROLLER_ANNOTATION = "org.springframework.stereotype.Controller";
    String REST_CONTROLLER_ANNOTATION = "org.springframework.web.bind.annotation.RestController";

    String REQUEST_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.RequestMapping";
    String POST_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PostMapping";
    String GET_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.GetMapping";
    String DELETE_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.DeleteMapping";
    String PATCH_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PatchMapping";
    String PUT_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PutMapping";
    String REQUEST_PARAM_TEXT = "org.springframework.web.bind.annotation.RequestParam";
    String REQUEST_HEADER_TEXT = "org.springframework.web.bind.annotation.RequestHeader";
    String PATH_VARIABLE_TEXT = "org.springframework.web.bind.annotation.PathVariable";
    String REQUEST_BODY_TEXT = "org.springframework.web.bind.annotation.RequestBody";

    String SWAGGER_ANNOTATIONS_API =   "io.swagger.annotations.Api";
    String SWAGGER_ANNOTATIONS_API_MODEL =   "io.swagger.annotations.ApiModel";
    String SWAGGER_ANNOTATIONS_API_MODEL_PROPERTY =   "io.swagger.annotations.ApiModelProperty";
    String SWAGGER_ANNOTATIONS_API_OPERATION =   "io.swagger.annotations.ApiOperation";
    String SWAGGER_ANNOTATIONS_API_IMPLICIT_PARAMS =   "io.swagger.annotations.ApiImplicitParams";

    String SWAGGER_ANNOTATIONS_NAME_API =   "Api";
    String SWAGGER_ANNOTATIONS_NAME_API_MODEL =   "ApiModel";
    String SWAGGER_ANNOTATIONS_NAME_API_MODEL_PROPERTY =   "ApiModelProperty";
    String SWAGGER_ANNOTATIONS_NAME_API_OPERATION =   "ApiOperation";
    String SWAGGER_ANNOTATIONS_NAME_API_IMPLICIT_PARAM =   "ApiImplicitParam";
    String SWAGGER_ANNOTATIONS_NAME_API_IMPLICIT_PARAMS =   "ApiImplicitParams";


}
