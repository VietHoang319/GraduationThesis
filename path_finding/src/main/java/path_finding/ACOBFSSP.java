package path_finding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ACOBFSSP {
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

	//Hàm tính giai thừa
	public int factorial(int number) {
		int fact = 1;
		if (number > 0) {
			for (int i = 1; i <= number;i++) {
				fact = fact * i;
			}
		}
		else if(number == 0) {
			fact = 1;
		}
		else {
			fact = 0;
		}
		return fact;
	}

	// Hàm tính tổ hợp chập
	public int combination(int number1, int number2) {
		int C;
		if (factorial(number1) <= 0) {
			C = 0;
		}
		else {
			C = factorial(number1) / (factorial(number1 - number2) * factorial(number2)); 
		}
		return C;
	}

	public double setNextNode(ArrayList<int[]> path) {
		double length_ij = 0;
		double denominator = 0; // Mẫu công thức tính xác suất chọn
		ArrayList<int[]> nodeList = new ArrayList<>();
		// Tọa độ điểm đang xét
		x = path.get(path.size() - 1)[0];
		y = path.get(path.size() - 1)[1];

		// Lấy các điểm xung quanh
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
		ArrayList<Double> P = new ArrayList<Double>();
		for (int i = 0; i < nodeList.size(); i++) {
			int count = 0;
			int x1 = nodeList.get(i)[0];
			int y1 = nodeList.get(i)[1];

			for (int j = 0; j < arrayListRandom.length; j++) {
				int a = arrayListRandom[j][0];
				int b = arrayListRandom[j][1];
				// x2, y2 là điểm nhìn thấy xung quanh điểm xuất hồn
				int x2 = a + x1;
				int y2 = b + y1;
				if (x2 < 0 || y2 < 0 || x2 > labelMatrix.length - 1 || y2 > labelMatrix.length - 1) {
					continue;
				}
				else if(binMatrix[x2][y2] == 1) {
					count = count + 1;
				}
			}
			// Tính tổ hợp chập của tường
			int combinationOfWall = combination(8, count);
			// Tính tổ hợp chập của số lối thoát
			int combinationOfExit = combination((8 - count - 1), 1);
			double sp = (double)combinationOfExit / (double)combinationOfWall;
			
			double eta = 1 / Math.sqrt(Math.pow((x - x1), 2) + Math.pow((y - y1), 2));
			double tau = pheromone[labelMatrix[x][y]][labelMatrix[x1][y1]];
			double m = Math.pow(tau, alpha) * Math.pow(eta, beta) * sp;
			numeratorList[i] = m;
			denominator = denominator + m;
		}

		// Tính danh sách xác suất chọn
		for (int i = 0; i < numeratorList.length; i++) {
			// xác suất chọn của từng điểm khả thi
			double pr = numeratorList[i]/denominator;
			P.add(pr);
		}
		//		System.out.println("Probability list:" + P);

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
			//		System.out.println("x: "+ a + ", y: "+ b);
			//		System.out.println(labelMatrix[a][b]);
//			System.out.println("\n");
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

	public void randomQ() {
		Q = random.nextInt(100) + 1;
	}

	// Hàm kiểm tra đường đi
	public double checkPath(ArrayList<int[]> path, double length) {
		ArrayList<int[]> copyPath = new ArrayList<>();
		ArrayList<int[]> queue = new ArrayList<>(); // Hàng đợi
		ArrayList<int[]> V = new ArrayList<>(); // Danh sách các đỉnh đã xét
		ArrayList<Integer> label_V = new ArrayList<>();

		if (path.size() > 1) {
			queue.add(path.get(0));
			V.add(path.get(0));
			label_V.add(labelMatrix[path.get(0)[0]][path.get(0)[1]]);
			while (queue.size() != 0) {
//				System.out.println("\nXét đỉnh: " + labelMatrix[queue.get(0)[0]][queue.get(0)[1]]);
				int[] v = queue.get(0);
				queue.remove(0);
				if (v[0] == path.get(path.size() - 1)[0] && v[1] == path.get(path.size() - 1)[1]) {
					break;
				}
				ArrayList<Integer> nodeIndexList = new ArrayList<>();
				for (int i = 0; i < arrayListRandom.length; i++) {
					int a = v[0] + arrayListRandom[i][0];
					int b = v[1] + arrayListRandom[i][1];
					if (a < 0 || b < 0 || a > labelMatrix.length - 1 || b > labelMatrix.length - 1
							|| binMatrix[a][b] == 1) {
						continue;
					} else {
						if (indexPath.contains(labelMatrix[a][b]) == true
								&& label_V.contains(labelMatrix[a][b]) == false) {
							label_V.add(labelMatrix[a][b]);
							int[] node = { a, b };
							V.add(node);
							queue.add(node);
//							System.out.println("Thêm đỉnh " + labelMatrix[a][b] + " vào hàng đợi.");
						}
					}
				}
			}

			// for (int i = 0; i < label_V.size(); i++) {
			// System.out.print("->" + label_V.get(i));
			// }

			int index = 0;
			for (int i = 0; i < label_V.size(); i++) {
				if (label_V.get(i) == labelMatrix[path.get(path.size() - 1)[0]][path.get(path.size() - 1)[1]]) {
					index = i;
					break;
				}
			}

			copyPath.add(V.get(index));
			while (labelMatrix[copyPath.get(copyPath.size() - 1)[0]][copyPath
					.get(copyPath.size() - 1)[1]] != labelMatrix[path.get(0)[0]][path.get(0)[1]]) {
				ArrayList<int[]> nodeIndexList = new ArrayList<>();
				for (int i = 0; i < arrayListRandom.length; i++) {
					int a = copyPath.get(copyPath.size() - 1)[0] + arrayListRandom[i][0];
					int b = copyPath.get(copyPath.size() - 1)[1] + arrayListRandom[i][1];

					if (a < 0 || b < 0 || a > labelMatrix.length - 1 || b > labelMatrix.length - 1
							|| binMatrix[a][b] == 1) {
						continue;
					} else {
						int[] node = { a, b };
						nodeIndexList.add(node);
					}
				}

				boolean flag = true;
				for (int i = 0; i < index; i++) {
					for (int j = 0; j < nodeIndexList.size(); j++) {
						if (V.get(i)[0] == nodeIndexList.get(j)[0] && V.get(i)[1] == nodeIndexList.get(j)[1]) {
							copyPath.add(V.get(i));
							flag = false;
							break;
						}
					}
					if (flag == false) {
						break;
					}
				}
			}

			Collections.reverse(copyPath);

			path.clear();
			indexPath.clear();
//			System.out.println("\nLength: " + length);
			length = 0;
			for (int i = 0; i < copyPath.size(); i++) {
//				System.out.print("->" + labelMatrix[copyPath.get(i)[0]][copyPath.get(i)[1]]);
				path.add(copyPath.get(i));
				indexPath.add(labelMatrix[copyPath.get(i)[0]][copyPath.get(i)[1]]);
			}
			for (int i = 0; i < path.size(); i++) {
				if (i + 1 < path.size()) {
					length = length + Math.sqrt(Math.pow((path.get(i)[0] - path.get(i + 1)[0]), 2)
							+ Math.pow((path.get(i)[1] - path.get(i + 1)[1]), 2));
				}
			}
		}
		return length;
	}

	public void generateSolutions(int[] start) {
		for (int i = 0; i < 50; i++) {
//			System.out.println("\nAnt's number: " + i);
			path.add(start);
			indexPath.add(labelMatrix[start[0]][start[1]]);

//			randomQ();
			randomPath(path);
			if (length_ik > 0) {
				length_ik = checkPath(path, length_ik);
				double deltaTau = Q / length_ik;
				for (int a = 0; a < indexPath.size(); a++) {
					if (a + 1 < indexPath.size()) {
						sumDeltaTau[indexPath.get(a)][indexPath.get(a + 1)] = sumDeltaTau[indexPath.get(a)][indexPath.get(a + 1)] + deltaTau;
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
		length = checkPath(finalPath, length);
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
