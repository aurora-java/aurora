package aurora.ide.api.statistics.cvs;

import java.util.HashMap;
import java.util.StringTokenizer;

public class CVSRepositoryLocation {

	public static final char COLON = ':';
	public static final char SEMICOLON = ';';
	public static final char HOST_SEPARATOR = '@';
	public static final char PORT_SEPARATOR = '#';
	public static int USE_DEFAULT_PORT = 0;
	private String methodName;
	private String userName;
	private String password;
	private String host;
	private int port;
	private String root;

	public CVSRepositoryLocation(String root) {
		this.fromString(root);
	}

	/**
	 * Parse a location string and return a CVSRepositoryLocation.
	 * 
	 * The valid format (from the cederqvist) is:
	 * 
	 * :method:[[user][:password]@]hostname[:[port]]/path/to/repository
	 * 
	 * However, this does not work with CVS on NT so we use the format
	 * 
	 * :method:[user[:password]@]hostname[#port]:/path/to/repository
	 * 
	 * Some differences to note: The : after the host/port is not optional
	 * because of NT naming including device e.g.
	 * :pserver:username:password@hostname#port:D:\cvsroot
	 * 
	 * Also parse alternative format from WinCVS, which stores connection
	 * parameters such as username and hostname in method options:
	 * 
	 * :method[;option=arg...]:other_connection_data
	 * 
	 * e.g. :pserver;username=anonymous;hostname=localhost:/path/to/repository
	 * 
	 * If validateOnly is true, this method will always throw an exception. The
	 * status of the exception indicates success or failure. The status of the
	 * exception contains a specific message suitable for displaying to a user
	 * who has knowledge of the provided location string.
	 * 
	 * @see CVSRepositoryLocation.fromString(String)
	 */
	public void fromString(String location) {
		try {
			// Get the connection method
			int start = location.indexOf(COLON);
			String methodName;
			int end;
			// For parsing alternative location format
			int optionStart = location.indexOf(SEMICOLON);
			HashMap hmOptions = new HashMap();

			if (start == 0) {
				end = location.indexOf(COLON, start + 1);

				// Check for alternative location syntax
				if (optionStart != -1) {
					// errorMessage =
					// CVSMessages.CVSRepositoryLocation_parsingMethodOptions;
					methodName = location.substring(start + 1, optionStart);
					// Save options in hash table
					StringTokenizer stOpt = new StringTokenizer(
							location.substring(optionStart + 1, end), "=;" //$NON-NLS-1$
					);
					while (stOpt.hasMoreTokens()) {
						hmOptions.put(stOpt.nextToken(), stOpt.nextToken());
					}
					start = end + 1;
				} else {
					methodName = location.substring(start + 1, end);
					start = end + 1;
				}
			} else {
				// this could be an alternate format for ext:
				// username:password@host:path
				methodName = "ext"; //$NON-NLS-1$
				start = 0;
			}

			// Get the user name and password (if provided)

			end = location.indexOf(HOST_SEPARATOR, start);
			String user = null;
			String password = null;
			// if end is -1 then there is no host separator meaning that the
			// username is not present
			// or set in options of alternative-style location string
			if (end != -1) {
				// Get the optional user and password
				user = location.substring(start, end);
				// Separate the user and password (if there is a password)
				start = user.indexOf(COLON);
				if (start != -1) {
					password = user.substring(start + 1);
					user = user.substring(0, start);
				}
				// Set start to point after the host separator
				start = end + 1;
			} else if (optionStart != -1) {
				// alternative location string data
				// errorMessage =
				// CVSMessages.CVSRepositoryLocation_parsingOptionsUsername;
				if (hmOptions.containsKey("username"))user = hmOptions.get("username").toString(); //$NON-NLS-1$ //$NON-NLS-2$
				// errorMessage =
				// CVSMessages.CVSRepositoryLocation_parsingOptionsPassword;
				if (hmOptions.containsKey("password"))password = hmOptions.get("password").toString(); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// Get the host (and port)
			end = location.indexOf(COLON, start);
			int hostEnd = end;
			if (end == -1) {
				// The last colon is optional so look for the slash that starts
				// the path
				end = location.indexOf('/', start);
				hostEnd = end;
				// Decrement the end since the slash is part of the path
				if (end != -1)
					end--;
			}
			String host = (hmOptions.containsKey("hostname")) ? hmOptions.get("hostname").toString() : location.substring(start, hostEnd); //$NON-NLS-1$ //$NON-NLS-2$
			int port = USE_DEFAULT_PORT;
			boolean havePort = false;
			if (hmOptions.containsKey("port")) { //$NON-NLS-1$
				port = Integer.parseInt(hmOptions.get("port").toString()); //$NON-NLS-1$
				havePort = true;
			}
			// Separate the port and host if there is a port
			start = host.indexOf(PORT_SEPARATOR);
			if (start != -1) {
				try {
					// Initially, we used a # between the host and port
					port = Integer.parseInt(host.substring(start + 1));
					host = host.substring(0, start);
					havePort = true;
				} catch (NumberFormatException e) {
					// Ignore this as the #1234 port could be part of a proxy
					// host string
				}
			}
			if (!havePort) {
				// In the correct CVS format, the port follows the COLON
				int index = end;
				char c = location.charAt(++index);
				String portString = new String();
				while (Character.isDigit(c)) {
					portString += c;
					c = location.charAt(++index);
				}
				if (portString.length() > 0) {
					end = index - 1;
					port = Integer.parseInt(portString);
				}
			}

			// Get the repository path (translating backslashes to slashes)
			start = end + 1;
			String root = location.substring(start);
			this.methodName = methodName;
			this.userName = user;
			this.password = password;
			this.host = host;
			this.port = port;
			this.root = root;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

}
