package aurora.ide.api.statistics.map;

import java.util.List;

import aurora.ide.api.statistics.model.ProjectObject;
import aurora.ide.api.statistics.model.StatisticsProject;

public class ObjectStatisticsResult {

	// bm,view,service
	private String type;

	private List<ProjectObject> objects;

	private StatisticsProject project;

	public ObjectStatisticsResult(StatisticsProject project, List<ProjectObject> objects, String type) {
		this.type = type;
		this.objects = objects;
		this.project = project;
	}

	public StatisticsProject getProject() {
		return project;
	}

	public void setProject(StatisticsProject project) {
		this.project = project;
	}

	public String getType() {
		return type;
	}

	public List<ProjectObject> getObjects() {
		return objects;
	}

	public int getCount() {
		return objects.size();
	}

	public int getTotalFileSize() {
		int size = 0;
		for (ProjectObject po : objects) {
			size += po.getFileSize();
		}
		return size;
	}

	public int getMaxFileSize() {
		int max = Integer.MIN_VALUE;
		for (ProjectObject po : objects) {
			max = Math.max(po.getFileSize(), max);
		}
		return max == Integer.MIN_VALUE ? 0 : max;
	}

	public int getMinFileSize() {
		int min = Integer.MAX_VALUE;
		for (ProjectObject po : objects) {
			min = Math.min(po.getFileSize(), min);
		}
		return min == Integer.MAX_VALUE ? 0 : min;
	}

	public int getAverageFileSize() {
		return this.getCount() != 0 ? this.getTotalFileSize() / this.getCount() : 0;
	}

	public int getTotalScriptSize() {
		int size = 0;
		for (ProjectObject po : objects) {
			size += po.getScriptSize();
		}
		return size;
	}

	public int getMaxScriptSize() {
		int max = Integer.MIN_VALUE;
		for (ProjectObject po : objects) {
			max = Math.max(po.getScriptSize(), max);
		}
		return max == Integer.MIN_VALUE ? 0 : max;
	}

	public int getMinScriptSize() {
		int min = Integer.MAX_VALUE;
		for (ProjectObject po : objects) {
			int scriptSize = po.getScriptSize();
			if (scriptSize != 0)
				min = Math.min(scriptSize, min);
		}
		return min == Integer.MAX_VALUE ? 0 : min;
	}

	public int getAverageScriptSize() {
		return this.getCount() != 0 ? this.getTotalScriptSize() / this.getCount() : 0;
	}

	public int getTotalTagCount() {
		int size = 0;
		for (ProjectObject po : objects) {
			size += po.getTags().size();
		}
		return size;
	}

	public int getMaxTagCount() {
		int max = Integer.MIN_VALUE;
		for (ProjectObject po : objects) {
			max = Math.max(po.getTags().size(), max);
		}
		return max == Integer.MIN_VALUE ? 0 : max;
	}

	public int getMinTagCount() {
		int min = Integer.MAX_VALUE;
		for (ProjectObject po : objects) {
			min = Math.min(po.getTags().size(), min);
		}
		return min == Integer.MAX_VALUE ? 0 : min;
	}

	public int getAverageTagCount() {
		return this.getCount() != 0 ? this.getTotalTagCount() / this.getCount() : 0;
	}

	public int getFileCount() {
		return objects.size();
	}

	@Override
	public String toString() {
		return type;
	}

	// public int getTotalTagCount() {
	// int size = 0;
	// for (ProjectObject po : objects) {
	// size += po.getTags().size();
	// }
	// return size;
	// }
	//
	// public int getMaxTagCount() {
	// int max = Integer.MIN_VALUE;
	// for (ProjectObject po : objects) {
	// max = Math.max(po.getTags().size(), max);
	// }
	// return max;
	// }
	//
	// public int getMinTagCount() {
	// int min = Integer.MAX_VALUE;
	// for (ProjectObject po : objects) {
	// min = Math.min(po.getTags().size(), min);
	// }
	// return min;
	// }
	//
	// public int getAverageTagCount() {
	// return this.getCount() != 0 ? this.getTotalTagCount()
	// / this.getCount() : 0;
	// }

}
