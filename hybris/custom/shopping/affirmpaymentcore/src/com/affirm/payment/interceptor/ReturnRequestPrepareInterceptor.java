package com.affirm.payment.interceptor;

import de.hybris.platform.returns.RMAGenerator;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

public class ReturnRequestPrepareInterceptor implements PrepareInterceptor<ReturnRequestModel> {

   @Resource(name = "RMAGenerator") private RMAGenerator rmaGenerator;

   @Override public void onPrepare(ReturnRequestModel returnRequestModel, InterceptorContext interceptorContext) throws InterceptorException {
      if(StringUtils.isEmpty(returnRequestModel.getRMA())){
         returnRequestModel.setRMA(rmaGenerator.generateRMA(returnRequestModel));
      }
   }

}
