package aurora.service.http;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.svg.PDFTranscoder;


public class ExportChartServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 3920224595120519682L;

	public ExportChartServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String type = request.getParameter("type");
		String svg = request.getParameter("svg");
		ServletOutputStream out = response.getOutputStream();
		if (null != type && null != svg) {
			// This line is necessary due to a bug in the highcharts SVG
			// generator for IE
			// I'm guessing it wont be needed later.
			svg = svg.replaceAll(":rect", "rect");
			String ext = "";
			Transcoder t = null;

			if (type.equals("image/png")) {
				ext = "png";
				t = new PNGTranscoder();
			} else if (type.equals("image/jpeg")) {
				ext = "jpg";
				t = new JPEGTranscoder();
				t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));
			} else if (type.equals("application/pdf")) {
				ext = "pdf";
				t = new PDFTranscoder();
			} else if (type.equals("image/svg+xml")) {
				ext = "svg";
			}

			response.addHeader("Content-Disposition","attachment; filename=chart." + ext);//TODO:修改文件名
			response.addHeader("Content-Type", type);

			if (null != t) {
				TranscoderInput input = new TranscoderInput(new StringReader(svg));
				TranscoderOutput output = new TranscoderOutput(out);
				try {
					t.transcode(input, output);
				} catch (TranscoderException e) {
					out.print("Problem transcoding stream. See the web logs for more details.");
					e.printStackTrace();
				}

			} else if (ext == "svg") {
				out.print(svg);
			} else {
				out.print("Invalid type: " + type);
			}
		} else {
			response.addHeader("Content-Type", "text/html");
			out.println("Usage:\n\tParameter [svg]: The DOM Element to be converted.\n\tParameter [type]: The destination MIME type for the elment to be transcoded.");
		}
		out.flush();
		out.close();
	}
}
