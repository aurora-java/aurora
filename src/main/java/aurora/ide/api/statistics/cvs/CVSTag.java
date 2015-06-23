/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Olexiy Buyanskyy <olexiyb@gmail.com> - Bug 76386 - [History View] CVS Resource History shows revisions from all branches
 *******************************************************************************/
package aurora.ide.api.statistics.cvs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import aurora.ide.api.statistics.Status;

/**
 * A tag in CVS gives a label to a collection of revisions. The labels can
 * represent a version, a branch, or a date.
 */
public class CVSTag {

	public final static int HEAD = 0;
	public final static int BRANCH = 1;
	public final static int VERSION = 2;
	public final static int DATE = 3;

	public static final CVSTag DEFAULT = new CVSTag();
	public static final CVSTag BASE = new CVSTag("BASE", VERSION); //$NON-NLS-1$
	public static final String VENDOR_REVISION = "1.1.1"; //$NON-NLS-1$
	public static final String HEAD_REVISION = "1"; //$NON-NLS-1$
	public static final String UNKNOWN_BRANCH = "<n/a>";
	public static final String HEAD_BRANCH = "HEAD"; //$NON-NLS-1$

	protected String name;
	protected String branchRevision;
	protected int type;

	private static final String DATE_TAG_NAME_FORMAT = "dd MMM yyyy HH:mm:ss Z";//$NON-NLS-1$
	private static final SimpleDateFormat tagNameFormat = new SimpleDateFormat(
			DATE_TAG_NAME_FORMAT, Locale.US);

	protected static synchronized String dateToTagName(Date date) {
		tagNameFormat.setTimeZone(TimeZone.getTimeZone("GMT"));//$NON-NLS-1$
		return tagNameFormat.format(date);
	}

	protected synchronized static Date tagNameToDate(String name) {
		if (name == null)
			return null;
		try {
			return tagNameFormat.parse(name);
		} catch (ParseException e) {

			return null;
		}
	}

	public CVSTag() {
		this("HEAD", HEAD_REVISION, HEAD); //$NON-NLS-1$
	}

	public CVSTag(String name, int type) {
		this.name = name;
		this.type = type;
		this.branchRevision = null;
	}

	public CVSTag(String name, String branchRevision, int type) {
		this.name = name;
		this.branchRevision = branchRevision;
		this.type = type;
	}

	// Write a date in local date tag format
	public CVSTag(Date date) {
		this(dateToTagName(date), DATE);
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof CVSTag))
			return false;

		CVSTag tag = ((CVSTag) other);
		if (getType() != tag.getType())
			return false;
		if (!getName().equals(tag.getName()))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns branch revision - unique number given for each branch. Each
	 * number may have several names (aliases) and define next sequence of
	 * commits For example HEAD branch revision is 1, branches created from HEAD
	 * will have 1.1.0.2, 1.1.0.4, etc... Branches created from another branch
	 * 1.1.1.2.0.2, 1.1.1.2.0.4, etc...
	 */
	public String getBranchRevision() {
		return branchRevision;
	}

	public int getType() {
		// TODO: getType() will not return accurate types for Tags retrieved
		// from the local CVS Entries file. See Bug: 36758
		return type;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public int compareTo(CVSTag other) {
		if (getType() == DATE && other.getType() == DATE) {
			Date date1 = asDate();
			Date date2 = other.asDate();
			if (date1 == null || date2 == null)
				return -1;
			return date1.compareTo(date2);
		}
		return getName().compareToIgnoreCase(other.getName());
	}

	public static boolean equalTags(CVSTag tag1, CVSTag tag2) {
		if (tag1 == null)
			tag1 = CVSTag.DEFAULT;
		if (tag2 == null)
			tag2 = CVSTag.DEFAULT;
		return tag1.equals(tag2);
	}

	public static Status validateTagName(String tagName) {
		if (tagName == null)

			return null;
		if (tagName.equals("")) //$NON-NLS-1$
			return null;
		if (!Character.isLetter(tagName.charAt(0)))
			return null;

		for (int i = 0; i < tagName.length(); i++) {
			char c = tagName.charAt(i);
			if (Character.isSpaceChar(c) || c == '$' || c == ',' || c == '.'
					|| c == ':' || c == ';' || c == '@' || c == '|')
				return null;
		}
		return Status.OK_STATUS;
	}

	/**
	 * Return the date this tag represents or <code>null</code> if the tag is
	 * not of type DATE.
	 * 
	 * @return the date of the tag or <code>null</code>
	 */
	public Date asDate() {
		return tagNameToDate(name);
	}

	public boolean isHeadTag() {
		return getType() == HEAD
				|| (getType() == VERSION && getName().equals("HEAD")); //$NON-NLS-1$
	}

	public boolean isBaseTag() {
		return getName().equals("BASE"); //$NON-NLS-1$
	}

	public static String getTagLabel(CVSTag tag) {

		if (tag == null) {
			return "HEAD";
		}

		switch (tag.getType()) {
		case CVSTag.HEAD:
			return tag.getName();
		case CVSTag.VERSION:
			return tag.getName() + " (Version)";
		case CVSTag.BRANCH:
			return tag.getName() + " (Branch)";
		case CVSTag.DATE:
			return tag.getName() + " (Date)";
		default:
			return tag.getName();
		}
	}
}
