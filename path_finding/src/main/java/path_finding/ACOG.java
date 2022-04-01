package path_finding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class ACOG {
	Random random = new Random();

	//Tham so dieu chinh luong pheromoneeee
	public double Q = 1.0;

	//Ma tran trong so với vật cả = -1
	public int labelMatrix[][] = {
			{1, 6, 11, 16, 21},
			{2, -1, -1, -1, 22},
			{3, 8, -1, -1, 23},
			{4, 9, 14, 19, 24},
			{5, 10, 15, 20, 25}
	};

	public int binMatrix[][] = new int[labelMatrix.length][labelMatrix.length];

	public double pheromone[][] = new double[labelMatrix.length][labelMatrix.length];

	// Tổng delta tau
	public double sumDeltaTau[][] = new double[pheromone.length][pheromone.length];

	public static ArrayList<Node> mWall = new  ArrayList<>();

	public double alpha = 1;

	public double beta = 5;

	// Tốc độ bay hơi của pheromoneeee
	public double p = 0.5;

	// Tọa độ
	public int x;
	public int y;

	// Diem bat dau
	public static int start[];
	// Diem ket thuc
	public static int end[];

	// mang duong di cua cac con kien
	public static ArrayList<int[]> path = new ArrayList<>();
	public ArrayList<Integer> indexPath = new ArrayList<>();
	public static ArrayList<int[]> finalPath = new ArrayList<>();
	public double length = 0;

	private static String s = "";
	private int count_rp = 0;
	double length_ik = 0;

	// ma tran random cac vi tri ma kien co the di trong ma tran
	public int arrayListRandom[][] = {
			{0, 1},
			{1, 1},
			{1, 0},
			{1, -1},
			{0, -1},
			{-1, -1},
			{-1, 0},
			{-1, 1}
	};

	public void setLabelMatrix(int[][] labelMatrix) {
		this.labelMatrix = labelMatrix;
		this.binMatrix = new int[labelMatrix.length][labelMatrix.length];
		int size = labelMatrix[labelMatrix.length - 1][labelMatrix.length - 1] + 1;
		this.pheromone = new double[size][size];
		this.sumDeltaTau = new double[size][size];
	}

	// Các vật cả sẽ bằng 0, các trọng số còn lại sẽ cộng thêm 1
	public void setBinMarix() {
		for (int i = 0; i < binMatrix.length; i++) {
			for(int j = 0; j < binMatrix.length; j++) {
				binMatrix[i][j] = 0;
			}
		}

		for(int i = 0; i < mWall.size(); i++) {
			x = mWall.get(i).getX();
			y = mWall.get(i).getY();
			binMatrix[x][y] = 1;
		}
	}

	public void setDeltaTau() {
		for(int i = 0; i < sumDeltaTau.length; i++) {
			for(int j = 0; j < sumDeltaTau.length; j++) {
				sumDeltaTau[i][j] = 0;
			}
		}
	}

	public void setPheromone(){
		for(int i = 0; i < pheromone.length; i++){
			for(int j = 0; j < pheromone.length; j++){
				if (i == j) {
					pheromone[i][j] = 0;
				}
				else {
					pheromone[i][j] = 0.1;
				}

				for(int k = 0; k < mWall.size(); k++) {
					x = mWall.get(k).getX();
					y = mWall.get(k).getY();
					int w = labelMatrix[x][y];

					if ((w + 1) < (pheromone.length - 1)) {
						pheromone[w][w + 1] = 0;
						pheromone[w + 1][w] = 0;
					}
					if ((w - 1) > -1) {
						pheromone[w][w - 1] = 0;
						pheromone[w - 1][w] = 0;
					}

					if ((w + labelMatrix.length + 1) < (pheromone.length - 1)) {
						pheromone[w][w + labelMatrix.length + 1] = 0;
						pheromone[w + labelMatrix.length + 1][w] = 0;
					}
					if ((w + labelMatrix.length) < (pheromone.length - 1)) {
						pheromone[w][w + labelMatrix.length] = 0;
						pheromone[w + labelMatrix.length][w] = 0;
					}
					if ((w + labelMatrix.length - 1) < (pheromone.length - 1)) {
						pheromone[w][w + labelMatrix.length - 1] = 0;
						pheromone[w + labelMatrix.length - 1][w] = 0;
					}

					if ((w - labelMatrix.length - 1) > -1) {
						pheromone[w][w - labelMatrix.length - 1] = 0;
						pheromone[w - labelMatrix.length - 1][w] = 0;
					}
					if ((w - labelMatrix.length) > -1) {
						pheromone[w][w - labelMatrix.length] = 0;
						pheromone[w - labelMatrix.length][w] = 0;
					}
					if ((w - labelMatrix.length + 1) > -1) {
						pheromone[w][w - labelMatrix.length + 1] = 0;
						pheromone[w - labelMatrix.length + 1][w] = 0;
					}
				}
			}
		}
	}

	public void printlabelMatrix() {
		System.out.println("Start: " + labelMatrix[start[0]][start[1]] + "[" + start[0] + "]" + "[" + start[1] + "]");
		System.out.println("End: " + labelMatrix[end[0]][end[1]] + "[" + end[0] + "]" + "[" + end[1] + "] \n");

		System.out.println("\nLabel matrix: ");

		for (int i = 0; i < labelMatrix.length; i++) {
			for (int j = 0; j < labelMatrix.length; j++) {
				int node = labelMatrix[i][j];
				s += (node + "\t");
			}
			s += "\n";
		}
		System.out.println("" + s);
		s = "";
	}

	public void printBinMatrix() {
		System.out.println("\nCoding matrix: ");
		for (int i = 0; i < binMatrix.length; i++) {
			for (int j = 0; j < binMatrix.length; j++) {
				int node = binMatrix[i][j];
				s += (node + "\t");
			}
			s += "\n";
		}
		System.out.println("" + s);
		s = "";
	}

	public void printPheromone() {
		System.out.println("\nPheromone matrix: ");
		s += ("\t");
		for (int i = 0; i < pheromone.length; i++) {
			s += (i + "\t");
		}
		s += "\n";

		for (int i = 0; i < pheromone.length; i++) {
			s += (i + "\t");
			for (int j = 0; j < pheromone.length; j++) {
				double ph = pheromone[i][j];
				// format double ve kieu 0.00
				ph = Double.parseDouble(new DecimalFormat("##.##").format(ph));
				s += (ph + "\t");
			}
			s += "\n";
		}
		System.out.println("" + s);
		s = "";
	}

	public double setNextNode(ArrayList<int[]> path) {
		double length_ij = 0;
		double denominator = 0; // Mẫu công thức tính xác suất chọn
		ArrayList<int[]> nodeList = new ArrayList<>();
		// Tọa độ điểm đang xét
		x = path.get(path.size() - 1)[0];
		y = path.get(path.size() - 1)[1];
		int [] vector_iend = {(end[0] - x), (end[1] - y)};
//		System.out.println(vector_iend[0] + ", " + vector_iend[1]);
		for (int i = 0; i < arrayListRandom.length; i++) {
			int x1 = x + arrayListRandom[i][0];
			int y1 = y + arrayListRandom[i][1];
			int[] node = {x1, y1};
			if (x1 < 0 || y1 < 0 || x1 > labelMatrix.length - 1 || y1 > labelMatrix.length - 1 || binMatrix[x1][y1] == 1 || indexPath.contains(labelMatrix[x1][y1]) == true) {
				continue;
			}
			else {
				nodeList.add(node);
			}
		}

//		System.out.println("Node list: ");
//		for (int i = 0; i < nodeList.size(); i++) {
//			System.out.println(labelMatrix[nodeList.get(i)[0]][nodeList.get(i)[1]]);
//		}

		double[] numeratorList = new double[nodeList.size()];	// Danh sách tử công thức tính xác suất
		int[] dotProductList = new int[nodeList.size()];
		ArrayList<Double> P = new ArrayList<Double>();
		for (int i = 0; i < nodeList.size(); i++) {
			int x1 = nodeList.get(i)[0];
			int y1 = nodeList.get(i)[1];
			int[] vector_ij = {(x1 - x), (y1 -y)};
//			System.out.println(vector_ij[0] + ", " + vector_ij[1]);
			int dotProduct = (vector_ij[0] * vector_iend[0]) + (vector_ij[1] * vector_iend[1]);
			double eta = 1 / Math.sqrt(Math.pow((x - x1), 2) + Math.pow((y - y1), 2));
			double tau = pheromone[labelMatrix[x][y]][labelMatrix[x1][y1]];
			double m = Math.pow(tau, alpha) * Math.pow(eta, beta);
			numeratorList[i] = m;
			denominator = denominator + m;
			dotProductList[i] = dotProduct;
		}

//		System.out.println(denominator);
		// Tính danh sách xác suất chọn
		for (int i = 0; i < numeratorList.length; i++) {
//			System.out.println(numeratorList[i]);
			// xác suất chọn của từng điểm khả thi
			double pr = (numeratorList[i]/denominator) + alpha * dotProductList[i];
			P.add(pr);
//			System.out.println("dot:" + dotProductList[i]);
		}
//				System.out.println("Probability list:" + P);

		int PListSize = P.size();

		if (PListSize != 0) {
			double max = P.get(0);
			for (int i = 1; i < P.size(); i++) {
				if (max < P.get(i)) {
					max = P.get(i);
				}
			}
			//		System.out.println(max);

			ArrayList<Integer> maxProbabilityList = new ArrayList<Integer>();
			for (int i = 0; i < P.size(); i++) {
				if (max == P.get(i)) {
					maxProbabilityList.add(i); // Danh sách index của các điểm xét có xác suất lớn
				}
			}

			ArrayList<int[]> maxNodeList = new ArrayList<>();
			for (int i = 0; i < nodeList.size(); i++) {
				for(int j = 0; j < maxProbabilityList.size(); j++) {
					if(i == maxProbabilityList.get(j)) {
						maxNodeList.add(nodeList.get(i));
					}
				}
			}

			int t = random.nextInt(maxNodeList.size());
			int a = maxNodeList.get(t)[0];
			int b = maxNodeList.get(t)[1];
//					System.out.println("x: "+ a + ", y: "+ b);
//					System.out.println(labelMatrix[a][b]);
			int[] note = {a, b};
			path.add(note);
			indexPath.add(labelMatrix[a][b]);
//			for (int i = 0; i < indexPath.size(); i++) {
//				if (i == 0) {
//					System.out.print(indexPath.get(i));
//				}
//				else {
//					System.out.print("->" + indexPath.get(i));
//				}
//			}
			return length_ij = Math.sqrt(Math.pow((x - a), 2) + Math.pow((y - b), 2));
		}
		else {
//			System.out.println("\nError");
			return 0;
		}
	}

	// Hàm xây dựng đường đi đường đi
	public void randomPath(ArrayList<int[]> path) {
		double length_ij = 0;
		boolean flag = true;

		do {
			flag = false;
			count_rp = count_rp + 1;
			if (count_rp < 1000) {
				for (int i = 0; i < 1000; i++) {
//					System.out.println("\nLoop: " + i);
					length_ij = setNextNode(path);
					if (length_ij == 0) {
						flag = true;
						length_ik = 0;
						path.clear();
						indexPath.clear();
						path.add(start);
						indexPath.add(labelMatrix[start[0]][start[1]]);
						break;
					}
					else {
						length_ik = length_ik + length_ij;
						// thoai man dieu kien tim thay dich thi cho dung vong lap
						if (path.get(path.size() - 1)[0] == end[0] && path.get(path.size() - 1)[1] == end[1]) {
							count_rp = 0;
							break;
						}
					}
				}
			}
			else {
				break;
			}

		} while (flag == true);
	}

	public void generateSolutions(int[] start) {
		for (int i = 0; i < 50; i++) {
//			System.out.println("\nAnt's number: " + i);
			path.add(start);
			indexPath.add(labelMatrix[start[0]][start[1]]);

			randomPath(path);
			if (length_ik > 0) {
				double deltatau = Q / length_ik;
				for (int a = 0; a < indexPath.size(); a++) {
					if (a + 1 < indexPath.size()) {
						sumDeltaTau[indexPath.get(a)][indexPath.get(a + 1)] = sumDeltaTau[indexPath.get(a)][indexPath.get(a + 1)] + deltatau;
					}
				}
				path.clear();
				indexPath.clear();
			}
			else {
				length_ik = 0;
			}
		}
	}

	// Hàm cập nhật pheromone
	public void updatePheromoneMatrix() {
		for (int i = 0; i < pheromone.length; i++) {
			for (int j = 0; j < pheromone.length; j++) {
				if (pheromone[i][j] != 0) {
					if (sumDeltaTau[i][j] != 0){
						pheromone[i][j] = (1 - p) * pheromone[i][j] + sumDeltaTau[i][j];
					}
				}
			}
		}
	}

	public void generateOfBestPath(int[] start) {
		finalPath.clear();
		finalPath.add(start);
		indexPath.add(labelMatrix[start[0]][start[1]]);
		randomPath(finalPath);
		for (int i = 0; i < finalPath.size(); i++) {
			if (i + 1 < finalPath.size()) {
				length = length + Math.sqrt(Math.pow((finalPath.get(i)[0] - finalPath.get(i+1)[0]), 2) + Math.pow((finalPath.get(i)[1] - finalPath.get(i+1)[1]), 2));
			}
		}
	}

	public ArrayList<int[]> main() {
		setBinMarix();
		setDeltaTau();
		setPheromone();
//		printlabelMatrix();
//		printBinMatrix();
//		printPheromone();
		for (int i = 0; i < 1; i++) {
			if (count_rp < 1000) {
				generateSolutions(start);
				updatePheromoneMatrix();
				generateOfBestPath(start);
			}
			else {
				break;
			}
		}
		return finalPath;
	}

	public ArrayList<Node> getPath_list() {
		ArrayList<Node> result = new ArrayList<>();

		for (int i = 0; i < finalPath.size(); i++) {
			int[] node = finalPath.get(i);
			if (i == 0) {
				result.add(new Node(0, finalPath.get(i)[0], finalPath.get(i)[1]));
			}
			else if (i == finalPath.size() - 1) {
				if (finalPath.get(i)[0] == end[0] && finalPath.get(i)[1] == end[1]) {
					result.add(new Node(1, finalPath.get(i)[0], finalPath.get(i)[1]));
				}
				else {
					result.add(new Node(5, node[0], node[1]));
				}
			}
			else {
				result.add(new Node(5, node[0], node[1]));
			}
		}
		return result;
	}
}
