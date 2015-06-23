package aurora.service.http;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 图形验证码.
 * @version $Id: ImageValidateServlet.java v 1.0 2010-8-12 下午07:15:04 IBM Exp $
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class ImageCodeServlet extends HttpServlet {
	
	public static final String VALIDATE_CODE = "validate_code";
	
	private static final long serialVersionUID = 1L;
	private static int WIDTH = 78;
	private static int HEIGHT = 20;
	private static int LENGTH = 5;

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		response.setContentType("image/jpeg");
		ServletOutputStream sos = response.getOutputStream();
//		response.setHeader("Pragma", "No-cache");
//		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("cache-control", "must-revalidate");
		response.setHeader("pragma", "public");	
		response.setHeader("Expires", "0");

		BufferedImage image = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		char[] rands = generateCheckCode();
		try{
			drawBackground(g);
			drawRands(g, rands);
			g.dispose();
			
			ImageIO.write(image, "jpg", sos);
		
			sos.flush();
		}finally{
			sos.close();
		}
		session.setAttribute(VALIDATE_CODE, new String(rands));
		//TODO:改成调用统一的缓存接口,不在session中处理
	}

	private static char[] generateCheckCode() {
		String chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
		char[] rands = new char[LENGTH];
		for (int i = 0; i < LENGTH; i++){
			int rand = (int) (Math.random() * 32);
			rands[i] = chars.charAt(rand);
		}
		return rands;
	}

	private void drawRands(Graphics g, char[] rands) {
		g.setColor(Color.BLACK);
		g.setFont(new Font(null, Font.ITALIC | Font.BOLD, 18));

		g.drawString("" + rands[0], 1, 17);
		g.drawString("" + rands[1], 16, 15);
		g.drawString("" + rands[2], 31, 18);
		g.drawString("" + rands[3], 46, 16);
		g.drawString("" + rands[4], 61, 14);
		//g.drawString("" + rands[5], 76, 19);
	}

	private void drawBackground(Graphics g) {
		g.setColor(new Color(0xFFFFFF));// 0xDCDCDC
		g.fillRect(0, 0, WIDTH, HEIGHT);

		for (int i = 0; i < 120; i++) {
			int x = (int) (Math.random() * WIDTH);
			int y = (int) (Math.random() * HEIGHT);
			int red = (int) (Math.random() * 255);
			int green = (int) (Math.random() * 255);
			int blue = (int) (Math.random() * 255);
			g.setColor(new Color(red, green, blue));
			g.drawOval(x, y, 1, 0);
		}

		g.drawLine(0, 5, 100, 5);
		g.drawLine(0, 10, 100, 10);
		g.drawLine(0, 15, 100, 15);
	}
}
