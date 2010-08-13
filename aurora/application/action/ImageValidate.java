package aurora.application.action;

import javax.servlet.http.HttpSession;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;
import aurora.service.http.ImageCodeServlet;
import aurora.service.validation.ImageValidationException;

public class ImageValidate extends AbstractEntry {
	
	private String code;

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap mContext = runner.getContext();
		HttpServiceInstance mService = (HttpServiceInstance) ServiceInstance.getInstance(mContext);
		HttpSession session = mService.getRequest().getSession();
		if(session!=null){
			String imageCode = (String) session.getAttribute(ImageCodeServlet.VALIDATE_CODE);
			String checkCode = TextParser.parse(this.getCode(), mContext);
			if(imageCode!=null && !imageCode.equalsIgnoreCase(checkCode))
			throw new ImageValidationException("验证码不正确");
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
