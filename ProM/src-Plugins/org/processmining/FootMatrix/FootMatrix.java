package org.processmining.FootMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.Data.MyEvent;
import org.processmining.Data.MyLog;
import org.processmining.Data.MyTrace;
import org.processmining.Gather.Triad;
import org.processmining.Gather.Tuple;
import org.processmining.Relation.Relation;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

public class FootMatrix {
	private Map<String, Transition> StoTmap;
	private Map<Integer, Transition> ItoTmap;
	private Map<Transition, Integer> TtoImap;
	private MyLog mylog;
	private PetrinetImpl pnimpl;
	private Set<Triad> allrelationset;
	private Set<Triad> directfollowset;
	//Transition g,h,c,f;
	//private Set<Triad> indirectfollowset;
	private Set<Triad> directcasualset;
	private Set<Triad> selfcycledirectset;
	private Set<Triad> selfcycleset;
	public Set<Triad> backpacksset;
	private Set<Triad> norelset;
	public Set<Tuple> paralleltuple = new HashSet<Tuple>();
	public ArrayList<Tuple> selfcycledirectlist = new ArrayList<Tuple>();
	public ArrayList<Tuple> selfcyclelist = new ArrayList<Tuple>();
	private Set<Triad> bpset;//添加并行集
	private Set<Triad> parallelset;
	public Set<String> allelementset = new HashSet<String>();//取出所有元素
	public Set<String> copycycleset = new HashSet<String>();//循环元素副本
	public Set<String> cycleset = new HashSet<String>();//取出所有循环元素
	public Set<String> choicenameset = new HashSet<String>();
	public Set<String> cyclenameset = new HashSet<String>();//获取的所有循环命名集合
	public Set<String> cyclenameset1 = new HashSet<String>();//获取的所有循环命名集合
	public Set<String> choiceset = new HashSet<String>();//取出所有选择元素
	public Set<String> parallset = new HashSet<String>();//取出所有并行元素

	public Set<String> traceelementset = new HashSet<String>();//取出迹中活动集合
	public Set<String> traceelementsetall = new HashSet<String>();//取出迹中活动集合
	public Set<String> cychset = new HashSet<String>();//循环选择活动集合
	public Set allset = new HashSet();
	public Set<String> copycyclenameset = new HashSet<String>();//获取的所有循环命名集合

	public Set<String> ceclestrset = new HashSet<String>();//存放自环的活动集

	public Set<Tuple> dfset = new HashSet<Tuple>();//直接跟随集合
	public Set<Tuple> alltupleset = new HashSet<Tuple>();
	private Relation[][] MatrixofRelation;

	private String[][] MatrixtoString;
	public double[] supportrely;
	public double[] confidencerely;
	public FindRely[] transrely;
	private org.processmining.Gather.Tuple tuple2;

	public FootMatrix(MyLog log, PetrinetImpl pn) {
		mylog = log;
		pnimpl = pn;
		StoTmap = new HashMap<String, Transition>();
		TtoImap = new HashMap<Transition, Integer>();
		ItoTmap = new HashMap<Integer, Transition>();
		setMap();
		initial();
		createBaseRelation();
		createRelation();
		createMatrix();
	}

	private void setMap() {
		ArrayList<Transition> translist = new ArrayList<Transition>(pnimpl.getTransitions());
		for (int i = 0; i < translist.size(); i++) {
			Transition t = translist.get(i);
			StoTmap.put(t.toString(), t);
			TtoImap.put(t, i);
			ItoTmap.put(i, t);
		}
	}

	private void initial() {
		allrelationset = new HashSet<Triad>();//所有关系集
		directfollowset = new HashSet<Triad>();//直接跟随关系集
		//indirectfollowset = new HashSet<Triad>();
		directcasualset = new HashSet<Triad>();//直接因果关系集
		backpacksset = new HashSet<Triad>();//回路集
		//indirectcasualset = new HashSet<Triad>();
		parallelset = new HashSet<Triad>();//并行集
		norelset = new HashSet<Triad>();// 特殊的节点集
		selfcycleset = new HashSet<Triad>();//自环集
		selfcycledirectset = new HashSet<Triad>();//自环直接跟随集
		//bpset= new HashSet<Triad>();
		int size = StoTmap.size();//变迁矩阵初始化大小
		MatrixofRelation = new Relation[size][size];
		MatrixtoString = new String[size + 1][size + 1];
		for (int i = 0; i < MatrixofRelation.length; i++) {
			for (int j = 0; j < MatrixofRelation.length; j++) {
				MatrixofRelation[i][j] = Relation.NoRel;

			}
		}
		MatrixtoString[0][0] = " ";

		//循环输出初始矩阵（添加）

		for (int i = 0; i < MatrixtoString.length; i++) {

			for (int j = 0; j < MatrixtoString.length; j++) {
				System.out.print(MatrixtoString[i][j] + " ");
			}
			System.out.println();
		}

		printArray2(MatrixofRelation);

	}

	private void createBaseRelation() {
		//输出所有的迹
		String[] tracestr1111 = new String[mylog.size()];

		for (int i = 0; i < mylog.size(); i++) {
			MyTrace testtrace = mylog.get(i);
			String ttry = null;
			for (int j = 0; j < testtrace.size(); j++) {

				MyEvent testeventtotalall = testtrace.get(j);

				if (j == 0) {
					ttry = testeventtotalall.toString();
				} else {
					ttry += testeventtotalall.toString();
				}
			}
			tracestr1111[i] = ttry;
		}
		for (int i = 0; i < mylog.size(); i++) {
			System.out.println("迹[" + i + "]" + tracestr1111[i]);
		}

		//	Set<Tuple> dfset = new HashSet<Tuple>();//直接跟随集合
		//Set<Tuple> idfset = new HashSet<Tuple>();
		for (int i = 0; i < mylog.size(); i++) {
			MyTrace trace = mylog.get(i); //日志中的迹
			for (int j = 0; j < trace.size() - 1; j++) {
				MyEvent event = trace.get(j);
				String eventstr = event.getName();
				Transition transition = StoTmap.get(eventstr);

				MyEvent event2 = trace.get(j + 1);
				String eventstr2 = event2.getName();
				Transition transition2 = StoTmap.get(eventstr2);
				//transition2.getAttributeMap().put(AttributeMap.LABEL, "newName0");
				Tuple tuple = new Tuple(transition, transition2);
				dfset.add(tuple);
			}
		}

		Iterator<Tuple> dirit = dfset.iterator();//设置迭代器来遍历数组。
		while (dirit.hasNext()) {
			Tuple t = (Tuple) dirit.next();
			Triad triad = new Triad(t, Relation.DirectFollow);
			directfollowset.add(triad);
		}
		System.out.println("直接跟随关系:" + directfollowset);
	}

	private void createRelation() {
		Set<Tuple> alltuple = new HashSet<Tuple>();//suoyoude er yuan zu
		Set<Tuple> alltuple1 = new HashSet<Tuple>();//suoyoude er yuan zu
		Set<Tuple> dirtuple = new HashSet<Tuple>();// zhijie gensui er yuan zu

		ArrayList<Triad> dirlist = new ArrayList<Triad>(directfollowset);

		for (int i = 0; i < dirlist.size(); i++) {
			dirtuple.add(dirlist.get(i).getTuple());
		}
		alltuple.addAll(dirtuple);

		alltuple1.addAll(dfset);
		//循环输出所有的直接跟随二元组（）添加	
		System.out.println("直接跟随二元组1:" + alltuple1);

		ArrayList<Tuple> alltuplelistm = new ArrayList<Tuple>(alltuple);

		for (int i = 0; i < alltuplelistm.size(); i++) {

			System.out.println("直接跟随二元组:" + alltuple);

		}

		//生成parallel关系
		Set<Tuple> parttuple = new HashSet<Tuple>();
		Set<Tuple> parttuple1 = new HashSet<Tuple>();
		Set<Tuple> paralleltuple = new HashSet<Tuple>();//并行活动集
		Tuple tuple = null;
		Tuple retuple = null;
		ArrayList<Tuple> alltuplelist = new ArrayList<Tuple>(alltuple);
		ArrayList<Tuple> alltuplelist1 = new ArrayList<Tuple>(alltuple);
		for (Tuple a : alltuplelist) {
			System.out.println("tuplelisttest:" + a);
		}
		while (alltuplelist.size() > 0) {

			tuple = alltuplelist.remove(0);

			allelementset.add(tuple.getFirst().toString());//把元素添加到所有集合中
			allelementset.add(tuple.getSecond().toString());

			retuple = new Tuple(tuple.getSecond(), tuple.getFirst());

			System.out.println();

			if (alltuplelist.contains(retuple)) {

				paralleltuple.add(tuple);
				//	paralleltuple.add(retuple);
				parallset.add(tuple.getFirst().toString());
				parallset.add(tuple.getSecond().toString());

				alltuplelist.remove(retuple);
				System.out.println("tuple." + tuple.getFirst().toString());
			} else {
				parttuple.add(tuple);//不含并发的直接跟随二元组
				parttuple1.add(tuple);//不含并发的直接跟随二元组
				System.out.println("tuple1." + tuple.getFirst().toString());
			}
		}

		/* 把所有元素集合转化为数组操作 */
		String[] allelementarr = new String[allelementset.size()];
		allelementset.toArray(allelementarr);
		System.out.println("allelementset:" + allelementset);
		for (int i = 0; i < allelementarr.length; i++) {
			System.out.printf(allelementarr[i] + "  ");
		}

		String[] tracestr = new String[mylog.size()];
		for (int i = 0; i < mylog.size(); i++) {
			MyTrace temptrace = mylog.get(i);
			String str = null;
			for (int j = 0; j < temptrace.size(); j++) {
				if (j == 0) {
					str = temptrace.get(j).toString();
				} else {
					str = str + temptrace.get(j).toString();
				}
			}
			tracestr[i] = str;
		}

		//循环输出tracestr所有日志
		for (int i = 0; i < mylog.size(); i++) {
			System.out.println("tracestr[" + i + "]" + tracestr[i]);
		}

		int[] logtotal = new int[allelementarr.length];//日志元素总计矩阵
		int[][] tracetotal = new int[mylog.size()][allelementarr.length];//轨迹元素计数矩阵
		for (int i = 0; i < allelementarr.length; i++) {
			logtotal[i] = 0;
			System.out.print(logtotal[i] + " ");
		} //初始化日志活动统计矩阵。

		System.out.println();

		for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < allelementarr.length; j++) {
				tracetotal[i][j] = 0;
				System.out.print(tracetotal[i][j] + " ");
			}
			System.out.println();
		} //初始化迹活动统计矩阵

		//输出所有迹元素
		for (int i = 0; i < mylog.size(); i++) {
			MyTrace testtrace = mylog.get(i);
			for (int j = 0; j < testtrace.size(); j++) {
				MyEvent testeventtotalall = testtrace.get(j);
				traceelementsetall.add(testeventtotalall.toString());
				//System.out.print("迹"+i + testtrace.get(j).toString());
			}
			System.out.print("迹元素" + i + traceelementsetall.toString());

			System.out.println();
		}

		//得到选择活动和循环活动

		Set<String> termchoiceset = new HashSet<String>();

		for (int i = 0; i < mylog.size(); i++) {
			MyTrace testtrace = mylog.get(i);
			traceelementset.clear();
			for (int j = 0; j < testtrace.size(); j++) {
				MyEvent testeventtotal = testtrace.get(j);
				traceelementset.add(testtrace.get(j).toString());
				//	System.out.println("testtrace.get(j)" + testtrace.get(j).toString());
				for (int k = 0; k < allelementarr.length; k++) {
					if (testeventtotal.toString().equals(allelementarr[k].toString())) {
						//判断是不是循环集
						if (tracetotal[i][k] != 0) {
							cycleset.add(allelementarr[k].toString());
						}
						tracetotal[i][k] = tracetotal[i][k] + 1;
						//System.out.print( tracetotal[i][k]+" ");	
					}
					System.out.print(tracetotal[i][k] + " ");

				}
				System.out.println();
			}

			//	System.out.println("traceelementset" + traceelementset);
			//	System.out.println("allelementset" + allelementset);
			termchoiceset.addAll(allelementset);
			termchoiceset.removeAll(traceelementset);
			choiceset.addAll(termchoiceset);
			//	System.out.println("choiceset0:" + choiceset);
		}

		System.out.println("choiceset1:" + choiceset);
		System.out.println("+++++++++cycleset:+++++++++" + cycleset);

		//统计迹中的活动出现次数（添加）
		for (int i = 0; i < allelementarr.length; i++) {
			for (int j = 0; j < mylog.size(); j++) {
				logtotal[i] += tracetotal[j][i];

			}
			System.out.print("迹中活动的总数" + allelementarr[i] + logtotal[i]);
		}
		System.out.println();

		//统计日志活动出现次数,若活动的次数小于日志的总长度，那么这个活动有可能是选择活动，（还有可能是循环活动）
		for (int i = 0; i < allelementarr.length; i++) {
			if (logtotal[i] < mylog.size()) {
				choiceset.add(allelementarr[i]);
			}
			System.out.printf("%d  ", logtotal[i]);

		}
		//从选择结构中去除循环活动，剩下选择结构活动
		choiceset.removeAll(cycleset);
		System.out.println("choicesetfinal" + choiceset);
		System.out.println("*******logtotal********,\n");
		//输出变迁数目矩阵
		for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < allelementarr.length; j++) {
				System.out.printf("%d ", tracetotal[i][j]);
			}
			System.out.println("\n");
		}
		/* 添加获取主循环活动和回调函数集合(tianjia ) */
		//Set<String> mainloopset = new HashSet<String>();
		//	Set<String> callbackset = new HashSet<String>();
		Set<String> loopstartset = new HashSet<String>();
		Set<String> loopendtset = new HashSet<String>();

		//for (int i = 0; i < cycleset.size(); i++) {
		/*
		 * Tuple tuple1 = null;
		 * 
		 * for (int j = 0; j < alltuplelist1.size(); j++) {
		 * 
		 * tuple1=alltuplelist1.remove(j); Transition s = tuple1.getSecond();
		 * Transition f = tuple1.getFirst(); if (cycleset.contains(s.toString())
		 * && !cycleset.contains(f.toString()) ) {
		 * 
		 * loopstartset.add(s.toString());
		 * 
		 * } j--; System.out.println("循环开始活动集:" + loopstartset);
		 * 
		 * }
		 */

		//}

		// 功能同上输出循环开始活动集（添加）
		String[] cycleelementarr = new String[cycleset.size()];
		cycleset.toArray(cycleelementarr);
		for (int i = 0; i < cycleset.size(); i++) {
			Tuple tuple1 = null;
			ArrayList<Tuple> alltuplelist2 = new ArrayList<Tuple>(alltuple);
			for (int j = 0; j < alltuplelist2.size(); j++) {

				tuple1 = alltuplelist2.remove(j);
				Transition s = tuple1.getSecond();
				Transition f = tuple1.getFirst();
				if (cycleelementarr[i].equals(s.toString()) && !cycleset.contains(f.toString())) {

					loopstartset.add(s.toString());

				}
				j--;
				System.out.println("循环开始活动集:" + loopstartset);

			}

		}
		ArrayList<Tuple> alltuplelist3 = new ArrayList<Tuple>(alltuple);
		//功能  输出循环结束活动 （添加

		Tuple tuple2 = null;
		for (int j = 0; j < alltuplelist3.size(); j++) {

			tuple2 = alltuplelist3.remove(j);
			Transition s = tuple2.getSecond();
			Transition f = tuple2.getFirst();
			if (cycleset.contains(f.toString()) && loopstartset.contains(s.toString())) {

				loopendtset.add(f.toString());

			}
			j--;
			System.out.println("循环结束活动集:" + loopendtset);

		}

		ArrayList<Tuple> alltuplelist4 = new ArrayList<Tuple>(alltuple);
		//匹配，循环中的开始活动和结束活动 （添加）
		Set<String> ceclestrset1 = new HashSet<String>();//存放所有循环活动集      
		Set<String> ceclestrset0 = new HashSet<String>();//存放循环活动集      
		Tuple tuple3 = null;
		String str0 = null;
		// String str1=null;
		for (int j = 0; j < alltuplelist4.size(); j++) {
			ceclestrset0.clear();
			tuple3 = alltuplelist4.remove(j);
			Transition s = tuple3.getSecond();
			Transition f = tuple3.getFirst();
			if (loopendtset.contains(f.toString()) && loopstartset.contains(s.toString())) {

				ceclestrset0.add(s.toString());
				ceclestrset0.add(f.toString());

				String str = s.toString() + f.toString();
				str0 = str;

				// ceclestrset1.add(str0);
				ceclestrset1.add(ceclestrset0.toString());

			}
			//		str1 +=str0;

			//str1.
			j--;
			System.out.println("循环结构活动串:" + ceclestrset1);
		}

		/*
		 * String[] cyclestartendarr = new String[ceclestrset1.size()];
		 * 
		 * Set<String> cyclestartendset = new HashSet<String>();
		 * 
		 * cyclestartendset.addAll(ceclestrset1);
		 * cyclestartendset.toArray(cyclestartendarr);
		 * 
		 * 
		 * for (int i = 0; i < cyclestartendarr.length; i++) {
		 * 
		 * 
		 * 
		 * String[] cyclestartendarr1 = new
		 * String[cyclestartendarr[i].length()];
		 * 
		 * Set<String> cyclestartendset1 = new HashSet<String>();
		 * 
		 * cyclestartendset1.add(cyclestartendarr[i]);
		 * cyclestartendset1.toArray(cyclestartendarr1);
		 * 
		 * 
		 * for (int j = 0; j < cyclestartendarr1.length; j++) {
		 * 
		 * System.out.println("循环结构开始活动和结束活动11:" + cyclestartendarr1[j]);
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * System.out.println("循环结构开始活动和结束活动:" + cyclestartendarr[i]); }
		 */

		/*
		 * String[] cyclestartendarr1 = new String[loopendtset.size()];
		 * 
		 * Set<String> cyclestartendset1 = new HashSet<String>();
		 * 
		 * cyclestartendset1.addAll(loopendtset);
		 * cyclestartendset1.toArray(cyclestartendarr1);
		 * 
		 * 
		 * for (int i = 0; i < cyclestartendarr1.length; i++) {
		 * 
		 * System.out.println("循环结构开始活动和结束活动:" + cyclestartendarr1[i]);
		 * 
		 * }
		 */

		//功能 取出循环中的所有因果关系（添加0
		//  String[] cycledrietsettoarr = new String[parttuple1.size()];
		// cycledrietset.add(parttuple1.toString());
		// cycledrietset.toArray(cycledrietsettoarr);

		ArrayList<Tuple> cycledrietsetlist = new ArrayList<Tuple>(parttuple1);
		Set<Tuple> cycledrietset = new HashSet<Tuple>();//循环结构中的直接因果集合
		for (int i = 0; i < cycledrietsetlist.size(); i++) {

			Tuple t = cycledrietsetlist.remove(i);
			Transition s = t.getSecond();
			Transition f = t.getFirst();
			if (cycleset.contains(s.toString()) && cycleset.contains(f.toString())) {

				cycledrietset.add(t);

			}
			i--;
		}

		System.out.println("循环结构的直接因果关系:" + cycledrietset);

		// ArrayList<Tuple> cycledrietsetlist1 = new ArrayList <Tuple>(cycledrietset);  

		Tuple tuple4 = null;
		Tuple tuple5 = null;
		//匹配，循环中的开始活动和结束活动
		String[] cyclesettoarr = new String[cycleset.size()];
		String[] loopstartsetarr = new String[loopstartset.size()];
		String[] loopendtsetarr = new String[loopendtset.size()];
		String[] loopstrarr = new String[loopstartset.size()];
		int sl = 0;
		TT: for (int i = 0; i < mylog.size(); i++) {
			MyTrace a = mylog.get(i);
			for (int j = 0; j < a.size(); j++) {
				if (cycleset.contains(a.get(j).toString()) && sl < cycleset.size()) {
					cyclesettoarr[sl++] = a.get(j).toString();

				}

			}

		}

		//将选择和循环活动集放在一起
		cychset.addAll(choiceset);
		cychset.addAll(cycleset);
		cycleset.toArray(cyclesettoarr);
		//输出循环活动数组
		for (int i = 0; i < cycleset.size(); i++) {
			System.out.println("new cyclesettoarr" + cyclesettoarr[i]);
		}

		//添加
		loopstartset.toArray(loopstartsetarr);
		loopendtset.toArray(loopendtsetarr);
		loopstartset.toArray(loopstrarr);

		String STR = null;
		/*
		 * for (int i = 0; i < loopstartsetarr.length; i++) {
		 * 
		 * ArrayList<Tuple> alltuplelist5 = new ArrayList<Tuple>(alltuple);
		 * 
		 * for (int m = 0; m < alltuplelist5.size(); m++) { String ls =
		 * loopstartsetarr[i]; // ceclestrset0.clear(); tuple4 =
		 * alltuplelist5.remove(m); Transition s = tuple4.getSecond();
		 * Transition f = tuple4.getFirst(); if (ls.equals(f.toString()) &&
		 * cycleset.contains(s.toString()) ) {
		 * 
		 * STR =ls+s.toString(); } m--; loopstrarr[i] =STR; } STR=null;
		 * System.out.println("每个循环的字符串"+loopstrarr[i]);
		 * 
		 * 
		 * }
		 */
		//功能：输出循环结构名，name（添加）

		for (int i = 0; i < loopstartsetarr.length; i++) {

			ArrayList<Tuple> alltuplelist5 = new ArrayList<Tuple>(alltuple);

			for (int m = 0; m < alltuplelist5.size(); m++) {
				String ls = loopstartsetarr[i];
				//   ceclestrset0.clear();
				tuple4 = alltuplelist5.remove(m);
				Transition s = tuple4.getSecond();
				Transition f = tuple4.getFirst();
				if (ls.contains(f.toString()) && cycleset.contains(s.toString())) {
					ArrayList<Tuple> cycledrietsetlist1 = new ArrayList<Tuple>(cycledrietset);
					STR = ls + s.toString();
					for (int j = 0; j < cycledrietsetlist1.size(); j++) {
						tuple5 = cycledrietsetlist1.remove(j);
						Transition ss = tuple5.getSecond();
						Transition ff = tuple5.getFirst();

						if (s == ff && !loopendtset.contains(ff.toString())) {

							STR = STR + ss.toString();
							ArrayList<Tuple> cycledrietsetlist2 = new ArrayList<Tuple>(cycledrietset);
							for (int n = 0; n < cycledrietsetlist2.size(); n++) {
								tuple5 = cycledrietsetlist2.remove(j);
								Transition sss = tuple5.getSecond();
								Transition fff = tuple5.getFirst();

								if (ss == fff && !loopendtset.contains(fff.toString())) {

									STR = STR + sss.toString();

								}

								n--;

							}

						}

						j--;

					}

				}
				m--;
				loopstrarr[i] = STR;
			}
			STR = null;
			System.out.println("每个循环的字符串" + loopstrarr[i]);

		}

		for (int j = 0; j < loopstrarr.length; j++) {

			cyclenameset1.add(loopstrarr[j].toString());
			System.out.println("所有循环名称集合" + cyclenameset1);
		}

		System.out.println("所有循环名称集合" + cyclenameset1);

		//证明定理：所有循环中的活动至少能找到一条回路。
		Tuple temptuple = null;
		Tuple endtetuple = null;

		for (int i = 0; i < cyclesettoarr.length; i++) {
			String tempstr = cyclesettoarr[i];
			String findstr = null;
			int flag = 0;//找到一条回路就停止的标记
			int flag1 = 0;
			System.out.println("tempstr:" + tempstr);
			System.out.println("alltuplelist1.size:" + alltuplelist1.size());

			//取出含有循环结构的二元组
			for (int j = 0; j < alltuplelist1.size() && flag == 0; j++) {
				System.out.println("进入第一层循环");
				if (cyclesettoarr[i].equals(alltuplelist1.get(j).getFirst().toString())
						&& cychset.contains(alltuplelist1.get(j).getSecond().toString())) {
					temptuple = alltuplelist1.get(j);
					if (flag1 == 0) {
						tempstr = tempstr + alltuplelist1.get(j).getSecond().toString();//数组和字符串的区别
					} else {
						tempstr = tempstr + "|" + alltuplelist1.get(j).getSecond().toString();
					}
					System.out.println("temptuple1:" + temptuple);
					flag1++;
				}
				if (cyclesettoarr[i].equals(alltuplelist1.get(j).getSecond().toString())) {
					endtetuple = alltuplelist1.get(j);
					System.out.println("endtetuple1:" + endtetuple);
				}
			} //找到该二元组。
				//findstr=fdcycle(temptuple,endtetuple,alltuplelist1,tempstr);

			aa: for (int m = 0; m < alltuplelist1.size() && flag == 0; m++) {
				int flag2 = 0;
				for (int k = 0; k < alltuplelist1.size(); k++) {
					System.out.println("进入第2层循环");
					if (temptuple.getSecond().equals(temptuple.getFirst())) {//放入自环集
						ceclestrset.add(tempstr);
						break aa;
					}
					if ((temptuple.getSecond().equals(endtetuple.getFirst())
							&& endtetuple.getSecond().equals(temptuple.getFirst()))) {
						tempstr = tempstr + endtetuple.getSecond().toString();
						ceclestrset.add(tempstr);//二度循环
						System.out.println("此时他俩相等");
						flag = 1;
						break;
					}
					if (temptuple.getSecond().toString().equals(alltuplelist1.get(k).getFirst().toString())
							&& cychset.contains(alltuplelist1.get(k).getSecond().toString())) {

						tempstr = tempstr + alltuplelist1.get(k).getSecond().toString();
						System.out.println("tempstr2:" + tempstr);
						System.out.println("alltuplelist[]k" + alltuplelist1.get(k));
						temptuple = new Tuple(temptuple.getFirst(), alltuplelist1.get(k).getSecond());
						System.out.println("temptuple2:" + temptuple);
						flag2++;
						break;
					}
				}
			}
		}
		System.out.println("ceclestrser:" + ceclestrset);

		copycycleset.addAll(cycleset);

		//找到一组循环并且进行命名
		for (int i = 0; i < cyclesettoarr.length; i++)//循环活动数组
		{
			System.out.println("寻找====" + cyclesettoarr[i]);
		}
		System.out.println("xunzhao++++" + copycycleset);
		//找到循环结构，并对其命名。
		for (int i = 0; i < cyclesettoarr.length && copycycleset.size() > 0; i++) {
			if (copycycleset.contains(cyclesettoarr[i])) {
				String str = cyclesettoarr[i];
				String str1 = cyclesettoarr[i];
				String str2 = cyclesettoarr[i];
				copycycleset.remove(str);
				System.out.println("str+" + str);
				for (int m = 0; m < alltuplelist1.size(); m++) {

					for (int k = 0; k < alltuplelist1.size(); k++) {
						if (alltuplelist1.get(k).getFirst().toString().equals(str2)
								&& copycycleset.contains(alltuplelist1.get(k).getSecond().toString())) {
							str = str + alltuplelist1.get(k).getSecond().toString();
							str2 = alltuplelist1.get(k).getSecond().toString();
							copycycleset.remove(alltuplelist1.get(k).getSecond().toString());
							System.out.println("str1+" + str + "str2" + str2);
						}
						if (alltuplelist1.get(k).getSecond().toString().equals(str1)
								&& copycycleset.contains(alltuplelist1.get(k).getFirst().toString())) {
							str = alltuplelist1.get(k).getFirst().toString() + str;
							str1 = alltuplelist1.get(k).getFirst().toString();
							copycycleset.remove(alltuplelist1.get(k).getFirst().toString());
							System.out.println("str2+" + str);
						}
					}
				}
				System.out.println("str3" + str);
				cyclenameset.add(str);
				System.out.println("xunzhao-----" + copycycleset);
			}
		}

		copycyclenameset.clear();
		copycyclenameset.addAll(cyclenameset);
		System.out.println("+++++++++++cyclenameset:" + cyclenameset);

		System.out.println("所有循环名称集合" + cyclenameset1);

		//找到选择名，处理选择结构
		Set<String> copychoiceset = new HashSet<String>();
		copychoiceset.addAll(choiceset);
		String[] choicesettoarr = new String[choiceset.size()];//把集合变成数组
		choiceset.toArray(choicesettoarr);

		for (int i = 0; i < choicesettoarr.length && copychoiceset.size() > 0
				&& copychoiceset.contains(choicesettoarr[i]); i++) {

			String str = choicesettoarr[i];
			String str1 = choicesettoarr[i];
			String str2 = choicesettoarr[i];
			copychoiceset.remove(str);
			System.out.println("str+" + str);
			//	for (int m = 0; m < alltuplelist1.size(); m++) {

			for (int k = 0; k < alltuplelist1.size(); k++) {

				if (alltuplelist1.get(k).getFirst().toString().equals(str2)
						&& copychoiceset.contains(alltuplelist1.get(k).getSecond().toString())) {
					copychoiceset.remove(alltuplelist1.get(k).getSecond().toString());
					str2 = alltuplelist1.get(k).getSecond().toString();
					str = str + str2;
					copychoiceset.remove(alltuplelist1.get(k).getSecond().toString());
					i = i + 1;
					System.out.println("choicestr1+" + str + "choicestr2" + str2);
					System.out.println("一层剩余" + copychoiceset);
				}
				if (alltuplelist1.get(k).getSecond().toString().equals(str1)
						&& copychoiceset.contains(alltuplelist1.get(k).getFirst().toString())) {
					copychoiceset.remove(alltuplelist1.get(k).getFirst().toString());
					str1 = alltuplelist1.get(k).getFirst().toString();
					str = str1 + str;
					i = i + 1;
					copychoiceset.remove(alltuplelist1.get(k).getFirst().toString());
					System.out.println("choicestr2+" + str);
					System.out.println("第二层str1" + str1);
				}
			}
			//}
			System.out.println("choicestr3" + str);
			choicenameset.add(str);
		}

		//choicenameset.addAll(copychoiceset);
		//  choicenameset.addAll(copychoiceset);

		copychoiceset.addAll(choicenameset);
		System.out.println("choicenameset:" + choicenameset);
		System.out.println("****copychoicenameset:" + copychoiceset);
		/** 找到了所有的选择结构和所有的循环结构*进一步来确定他们三者之间的的关系 **/

		//将循环名集合转化为数组,将选择结构名准化为数组
		String[] choicenamesettoarr = new String[choicenameset.size()];//将选择名集合转化成数组
		choicenameset.toArray(choicenamesettoarr);
		String[] cyclenamesettoarr = new String[cyclenameset.size()];//将循环名转化为数组
		cyclenameset.toArray(cyclenamesettoarr);
		/* 统计循环结构的数目和循环活动的数目 */
		int[] cyclemaxarr = new int[cyclenamesettoarr.length];//转化成循环数组的个数数组
		int[] cycleelementmax = new int[cycleset.size()];//循环活动的存放数目的数组

		for (int i = 0; i < cyclemaxarr.length; i++) {//对数组进行初始化
			cyclemaxarr[i] = 0;
			System.out.println(cyclemaxarr[i]);
		}
		for (int i = 0; i < cycleelementmax.length; i++) {
			cycleelementmax[i] = 0;
		}

		//添加 功能 实现对循环活动名个数和循环结构名称统计	

		// 将选择结构添加进去 找到三者之间的关系（继续添加）

		Set<String> loopchset = new HashSet<String>();//循环选折活动集	

		loopchset.addAll(loopendtset);

		////	loopchset.addAll(choiceset);

		loopchset.addAll(choicenameset);

		////String[] loopchoicetoarr = new String[choiceset.size()+loopendtset.size()];
		String[] loopchoicetoarr = new String[choicenameset.size() + loopendtset.size()];

		loopchset.toArray(loopchoicetoarr);

		int[][] loopstructtotal = new int[mylog.size()][loopendtsetarr.length];//循环结构名称统计矩阵	

		for (int i = 0; i < mylog.size(); i++) {

			for (int j = 0; j < loopendtsetarr.length; j++) {

				loopstructtotal[i][j] = 0;

			}
		}

		for (int i = 0; i < loopendtsetarr.length; i++) {

			String le = loopendtsetarr[i];

			for (int j = 0; j < mylog.size(); j++) {
				MyTrace trace1 = mylog.get(j);
				for (int k = 0; k < trace1.size(); k++) {
					MyEvent event1 = trace1.get(k);
					if (event1.toString() == loopendtsetarr[i]) {

						loopstructtotal[j][i] = loopstructtotal[j][i] + 1;
					}

				}

			}
		}

		for (int i = 0; i < mylog.size(); i++) {

			for (int j = 0; j < loopendtsetarr.length; j++) {

				System.out.print(loopstructtotal[i][j] + " ");

			}
			System.out.println();

		}

		// 将选择结构添加进去 找到三者之间的关系（继续添加） 

		int[][] loopchoicestructtotal = new int[mylog.size()][loopendtsetarr.length + choicenamesettoarr.length];//循环结构名称统计矩阵	

		for (int i = 0; i < mylog.size(); i++) {

			for (int j = 0; j < loopendtsetarr.length + choicenamesettoarr.length; j++) {

				loopchoicestructtotal[i][j] = 0;
				System.out.print(loopchoicestructtotal[i][j] + " ");
			}
			System.out.println();
		}
		//////////////////////////////////
		/////在这出了问题因为在发现的循环结构顺寻是反的，所以会出问题
		///////////////////////
		for (int i = 0; i < loopchoicetoarr.length; i++) {

			String le = loopchoicetoarr[i];
			// String ch = loopendtsetarr[i];

			for (int j = 0; j < mylog.size(); j++) {
				MyTrace trace1 = mylog.get(j);
				for (int k = 0; k < trace1.size(); k++) {
					MyEvent event1 = trace1.get(k);

					if (event1.toString() == loopchoicetoarr[i]) {

						loopchoicestructtotal[j][i] = loopchoicestructtotal[j][i] + 1;

					}
				}

			}
		}

		for (int i = 0; i < mylog.size(); i++) {

			for (int j = 0; j < loopchoicetoarr.length; j++) {

				System.out.print(loopchoicestructtotal[i][j] + " ");

			}
			System.out.println();

		}

		//想统计出每条迹中循环出现的次数和选择出现的次(添加)

		for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < loopchoicetoarr.length; j++) {

				String lcc = loopchoicetoarr[j];

				System.out.print(lcc + loopchoicestructtotal[i][j] + " ");
			}

			System.out.println();
		}

		//再次确认循环活动集中的活动是不是在有些迹中出现多次，统计出循环活动在同一条迹中出现的最多次数	

		for (int i = 0; i < cyclesettoarr.length; i++) {
			String temp = cyclesettoarr[i];
			int max = 0;
			for (int j = 0; j < mylog.size(); j++) {
				int fromIndex = 0;
				int count = 0;
				while (true) {
					int index = tracestr[j].indexOf(temp, fromIndex);//返回从 fromIndex 位置开始查找指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1。
					if (-1 != index) {
						fromIndex = index + 1;
						count++;
					} else {
						break;
					}
				}
				if (count > max) {
					max = count;
				}
			}
			cycleelementmax[i] = max;
		}
		for (int i = 0; i < cycleelementmax.length; i++) {
			System.out.println("循环元素数组" + cycleelementmax[i]);
		}

		for (int i = 0; i < cyclenamesettoarr.length; i++) {
			for (int j = 0; j < cycleelementmax.length; j++) {
				cyclemaxarr[i] = cycleelementmax[0];
				if (cyclenamesettoarr[i].contains(cyclesettoarr[j]) && cycleelementmax[j] < cyclemaxarr[i]) {
					cyclemaxarr[i] = cycleelementmax[j];
				}
			}
		}
		/*
		 * for (int i = 0; i < cyclenamesettoarr.length; i++) { int max = 0; for
		 * (int j = 0; j < mylog.size(); j++) { int fromIndex = 0; int count =
		 * 0; while (true) { int index =
		 * tracestr[j].indexOf(cyclenamesettoarr[i], fromIndex); if (-1 !=
		 * index) { fromIndex = index + 1; count++; } else { break; } } if
		 * (count > max) { max = count; }
		 * 
		 * }
		 * 
		 * cyclemaxarr[i] = max; }
		 */
		for (int i = 0; i < cyclemaxarr.length; i++) {
			System.out.println("最后" + cyclemaxarr[i]);
		}
		/*
		 * cyclenameset.clear(); for (int i = 0; i < cyclenamesettoarr.length;
		 * i++) { cyclenameset.add(cyclenamesettoarr[i] + ":0"); } for (int i =
		 * 0; i < cyclenamesettoarr.length; i++) {
		 * //System.out.println(cyclemaxarr[i]); for (int j = 0; j <
		 * cyclemaxarr[i]; j++) { cyclenameset.add(cyclenamesettoarr[i] + ":" +
		 * (j + 1)); } } System.out.println("带有循环次数的循环名集合" + cyclenameset);
		 */

		System.out.println("所有循环名称集合" + cyclenameset1);

		//loopstrarr 循环数组名

		int total = 0;
		//切分trace
		//循环
		String[] cyclenumbersettoarr = new String[cyclenameset1.size()];
		cyclenameset1.toArray(cyclenumbersettoarr);
		total = cyclenameset1.size();
		Set<FindRely> rely = new HashSet();
		ArrayList<FindRely> rely11 = new ArrayList<>();

		Set<FindRely> copyrely = new HashSet();

		for (int i = 0; i < cyclenameset1.size(); i++) {
			System.out.println("cyclenumbersettoarr+" + i + cyclenumbersettoarr[i]);
		}

		/////////////////////////////////////寻找循环的就近选择依赖(zhuyi)
		System.out.println("寻找循环影响的选择++++++++++++++++++++++++++");

		// 如果两个循环时顺序是反的，那么将其反过来。
		for (int j = 0; j < mylog.size(); j++) {
			int t1 = 0;
			int t2 = 0;

			for (int i = 0; i < loopstrarr.length; i++) {
				t1 = tracestr[j].indexOf(loopstrarr[i], 0);
				t2 = tracestr[j].indexOf(loopstrarr[loopstrarr.length - 1], 0);
				if (tracestr[j].contains(loopstrarr[i]) && tracestr[j].contains(loopstrarr[loopstrarr.length - 1])
						&& t1 > t2) {

					swapArr(loopstrarr, 0, 1);
				}

			}
		}
		///	int index = tracestr[j].indexOf(temp, fromIndex);//返回从 fromIndex 位置开始查找指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1。
		//考虑两个循环的关系影响 暂时 添加
		/* 功能 这个地方是得到每条迹中的循环和选择活动影响的后一个循环的次数 */
		for (int i = 0; i < loopstrarr.length - 1; i++) {

			System.out.println("进入第1层");
			for (int j = 0; j < tracestr.length; j++) {

				FindRely re = new FindRely();//实例化一个关系依赖对象

				re.setFlow1(null);
				re.setNumber(0);
				re.setNumber0(0);
				re.setNumber1(0);
				re.setStr(null);
				re.setStr0(null);
				re.setStr1(null);
				if (!tracestr[j].contains(loopstrarr[i])) {
					continue;
				}
				System.out.println("进入第2层");
				String str = loopstrarr[i];
				String[] cut = tracestr[j]
						.split(/* "("+ */loopstrarr[i]/* +")+" */);//用循环去切割
				String[] cut1 = tracestr[j]
						.split(/* "("+ */loopstrarr[i]/* +")+" */);//用循环去切割
				int pos = 0;

				for (int s = 1; s < cut.length; s++) {
					if (!cut[s].isEmpty()) {

						System.out.println("cut[" + s + "]:" + cut[s]);

						re.setStr(str);//赋初值

						re.setNumber(cut.length - 1);//设置大小。迹中循环的数目循环次数加1
						System.out.println("......" + re.toString());

						for (int r = 0; r < choicenamesettoarr.length; r++) {
							String[] cutnext0 = null;
							if (cut.length < 2) {
								System.out.println("不包含");
								continue;
							} else

							//这里不应该是CUT1
							{
								if (cut[cut.length - 1].contains(choicenamesettoarr[r])) {
									System.out.println("包含");
									cutnext0 = cut1[cut.length - 1].split(choicenamesettoarr[r]);//用选择序列循环去切割	

									re.setStr0(String.valueOf(choicenamesettoarr[r]));
									re.setNumber0(1);//设置大小。

									System.out.println("......" + re.toString());
									//	break;

									for (int k = 1; k < loopstrarr.length; k++) {
										if (cutnext0[1].contains(loopstrarr[k])) {
											String[] cutnext = cutnext0[1].split(loopstrarr[i + 1]);//用后边的循环循环去切割

											for (int l = 0; l < cutnext.length; l++) {
												System.out.println("第二次切割的的值" + cutnext[l]);
											}
											if (cutnext.length < 2) {
												System.out.println("不包含");
												continue;
											} else {
												re.setNumber1(cutnext.length - 1);
												re.setFlow1(String.valueOf(loopstrarr[i + 1]));//受影响的循环

												System.out.println("......" + re.toString());

												break;
											}
										}

									}

								}

							}

						}

					}
				}

				System.out.println("添加这个类");
				rely.add(re);
				rely11.add(re);
				System.out.println("添加这个类11" + rely);
				System.out.println("添加这个类rely11" + rely11);

			}
		}
		/*
		 * 这个地方只是适合选择结构中含有一个活动的情况，若选择分支含有变迁序列那么，用上边的代码
		 * 
		 * for (int m = 0; m < cut[s].length(); m++) {
		 * System.out.println("cut[s].substring(m)" + cut[s].substring(m, m +
		 * 1));//s输出子集 if (choicenameset.contains(cut[s].substring(m, m + 1))) {
		 * //System.out.println("p" + p); System.out.println("包含");
		 * re.setStr0(String.valueOf(cut[s].charAt(m)));
		 * re.setNumber0(1);//设置大小。
		 * //re.addFlow(String.valueOf(cut[s].charAt(m)));
		 * System.out.println("......" + re.toString()); break; } else {
		 * System.out.println("不包含"); } //}
		 * 
		 * 
		 * //想想这个地方是不是应该把下边包含进去
		 */

		//添加循环结构 后边的循环结构					

		/*
		 * for (int k = 1; k < loopstrarr.length; k++) {
		 * 
		 * String[] cutnext = cutnext0[1].split(loopstrarr[i+1]);//用后边的循环循环去切割
		 * 
		 * for (int l = 0; l < cutnext.length; l++) {
		 * System.out.println("第二次切割的的值"+cutnext[l]); } if (cutnext.length<2){
		 * System.out.println("不包含"); continue; } else {
		 * re.setNumber1(cutnext.length-1);
		 * re.setFlow1(String.valueOf(loopstrarr[i+1]));//受影响的循环
		 * 
		 * System.out.println("......" + re.toString()); // rely.add(re); break;
		 * }
		 * 
		 * 
		 * }
		 */

		//统计循环加选择影响循环的依赖数目		（添加）
		//循环和选择影响循环

		FindRely[] relysettoarr = new FindRely[rely.size()];
		rely.toArray(relysettoarr);

		int relysetcount[] = new int[rely.size()]; //依赖计数
		for (int k = 0; k < rely.size(); k++) {
			relysetcount[k] = 0;
			System.out.println("依赖数目relysetcount" + k + relysetcount[k]);

		}

		FindRely[] loopchoicerelylooptoarr1 = new FindRely[rely11.size()];
		rely11.toArray(loopchoicerelylooptoarr1);
		int loopchoicerelyloopcount[] = new int[rely.size()]; //依赖计数
		for (int k = 0; k < rely.size(); k++) {
			loopchoicerelyloopcount[k] = 0;
			System.out.println("依赖数目loopchoicelooprelycount" + k + loopchoicerelyloopcount[k]);

		}

		for (int i = 0; i < relysettoarr.length; i++) {
			System.out.println("第一次循环0000000");
			for (int j = 0; j < loopchoicerelylooptoarr1.length; j++) {
				System.out.println("第二次循环0000000");
				if (relysettoarr[i].equals(loopchoicerelylooptoarr1[j])) {

					loopchoicerelyloopcount[i]++;

				}

			}

		}

		//关联规则处理

		supportrely = new double[relysettoarr.length];
		confidencerely = new double[relysettoarr.length];
		transrely = new FindRely[rely.size()];//新建一个对象
		System.arraycopy(relysettoarr, 0, transrely, 0, relysettoarr.length);//把一个数组复制到另一个数组里面去

		for (int i = 0; i < relysettoarr.length; i++) {
			System.out.println("transrely" + i + ":" + transrely[i]);
		}
		System.out.println("mylog.size" + mylog.size());
		//这一依赖在整个日志中至少要出现
		for (int i = 0; i < relysettoarr.length; i++) {
			System.out.println("relycount" + relysettoarr[i]);
			supportrely[i] = (double) loopchoicerelyloopcount[i] / (double) mylog.size();
			System.out.println("supportrely" + supportrely[i]);
		}
		for (int i = 0; i < relysettoarr.length; i++) {
			int count = 0;
			for (int j = 0; j < relysettoarr.length; j++) {
				if (relysettoarr[i].getStr().equals(relysettoarr[j].getStr())
						&& cyclenameset1.contains(relysettoarr[i].getStr())
						&& relysettoarr[i].getStr0().equals(relysettoarr[j].getStr0())
						&& relysettoarr[i].getNumber() == relysettoarr[j].getNumber()
						&& relysettoarr[i].getNumber0() == relysettoarr[j].getNumber0()) //循环+选择 影响  循环
				{
					count += loopchoicerelyloopcount[j];
				}
				//			if(relytoarr[i].getStr().equals(relytoarr[j].getStr())&&cyclenameset.contains(relytoarr[i].getStr())&&relytoarr[i].getNumber()==relytoarr[j].getNumber())//循环影响其他的
				//				{、
				//					count+=relycount[j];
				//				}
			}
			System.out.println("count" + count);
			System.out.println("relycount" + loopchoicerelyloopcount[i]);
			confidencerely[i] = (double) loopchoicerelyloopcount[i] / (double) count;
			System.out.println("confidencerely" + confidencerely[i]);
		}

		for (int k = 0; k < rely.size(); k++) {
			System.out.println("final" + relysettoarr[k] + ":" + loopchoicerelyloopcount[k] + "supportrely:"
					+ supportrely[k] + "confidencerely:" + confidencerely[k]);
		}

		for (int k = 0; k < rely.size(); k++) {
			if (confidencerely[k] == 1.0 && supportrely[k] > 0 && cyclenameset1.contains(relysettoarr[k].getStr())
					&& choicenameset.contains(relysettoarr[k].getStr0())
					&& cyclenameset1.contains(relysettoarr[k].getFlow1())) {
				System.out.println("loop(" + relysettoarr[k].getStr() + "," + relysettoarr[k].getNumber() + ")" + "∧"
						+ "choice(" + relysettoarr[k].getStr0() + "," + relysettoarr[k].getNumber0() + ")" + "=>"
						+ "loop(" + relysettoarr[k].getFlow1() + "," + relysettoarr[k].getNumber() + ")");
			}

			//			if(confidencerely[k]==1.0 && supportrely[k]>0 && choicenameset.contains(relytoarr[k].getStr()) && cyclenameset.contains(relytoarr[k].getFlow().get(0)))
			//			{
			//				System.out.println("choice("+relytoarr[k].getStr()+")"+"=>"+"loop("+relytoarr[k].getFlow().get(0)+","+relytoarr[k].getNumber()+")");
			//			}
			//			if(confidencerely[k]==1.0 && supportrely[k]>0 && choicenameset.contains(relytoarr[k].getStr()) && choicenameset.contains(relytoarr[k].getFlow().get(0)))
			//			{
			//				System.out.println("choice("+relytoarr[k].getStr()+")"+"=>"+"choice("+relytoarr[k].getFlow()+")");
			//			}
			//			if(confidencerely[k]==1.0 && supportrely[k]>0 && cyclenameset.contains(relytoarr[k].getStr()))
			//			{
			//				System.out.println("loop("+relytoarr[k].getStr()+","+relytoarr[k].getNumber()+")=>"+"choice("+relytoarr[k].getFlow().get(0)+")");
			//			}

		}

		System.out.println("dirtuple" + dirtuple);//直接跟随二元组
		System.out.println("parttuple" + parttuple);//除去并发的直接跟随二元组，即直接因果关系二元组
		Set<Tuple> dircasualtuple = new HashSet<Tuple>(dirtuple);
		dircasualtuple.retainAll(parttuple);//把parttuple中的元素放入dircasualtuple中
		System.out.println("dircasualtuple" + dircasualtuple);

		ArrayList<Tuple> parallellist = new ArrayList<Tuple>(paralleltuple);
		ArrayList<Tuple> dircasuallist = new ArrayList<Tuple>(dircasualtuple);
		ArrayList<Tuple> copydircasuallist = new ArrayList<Tuple>(dircasualtuple);

		//ArrayList<Tuple> indircasuallist = new ArrayList<Tuple>(indircasualtuple);
		for (int i = 0; i < parallellist.size(); i++) {
			Tuple t = parallellist.get(i);

			parallelset.add(new Triad(t, Relation.Parallel));
			////////	bpset.add(new Triad(t, Relation.Parallel));
		}
		Transition temp = null;
		for (int i = 0; i < copydircasuallist.size(); i++) {
			Tuple t1 = copydircasuallist.get(i);
			if (t1.getFirst().equals(t1.getSecond())) {
				copydircasuallist.remove(t1);
				dircasuallist.remove(t1);
				temp = t1.getFirst();
				selfcyclelist.add(t1);
			}
		}
		//是不是起到了作用?
		for (int i = 0; i < copydircasuallist.size(); i++) {
			Tuple t2 = copydircasuallist.get(i);
			if (t2.getFirst().equals(temp)) {
				dircasuallist.remove(t2);
				selfcycledirectlist.add(t2);
			}
			if (t2.getSecond().equals(temp)) {
				dircasuallist.remove(t2);
				selfcycledirectlist.add(t2);
			}
		}
		System.out.println("selfcyclelist" + selfcyclelist);
		System.out.println("selfcycledirectlistfinal" + selfcycledirectlist);
		System.out.println("dircasuallistfinal" + dircasuallist);
		for (int i = 0; i < dircasuallist.size(); i++) {
			Tuple t = dircasuallist.get(i);
			directcasualset.add(new Triad(t, Relation.DirectCasually));
		}
		for (int i = 0; i < selfcycledirectlist.size(); i++) {
			Tuple t = selfcycledirectlist.get(i);
			if (selfcycledirectlist.size() != 0) {
				selfcycledirectset.add(new Triad(t, Relation.DirectCasually));
			}
		}
		for (int i = 0; i < selfcyclelist.size(); i++) {
			Tuple t = selfcyclelist.get(i);
			selfcycleset.add(new Triad(t, Relation.Selfcycle));
		}

		for (int i = 0; i < ItoTmap.size(); i++) {
			Transition tra = ItoTmap.get(i);
			for (int j = 0; j < selfcyclelist.size(); j++)
				if (tra != selfcyclelist.get(j).getFirst()) {
					Tuple tup = new Tuple(tra, tra);
					norelset.add(new Triad(tup, Relation.NoRel));
				}
		}
		allrelationset.addAll(directcasualset);
		allrelationset.addAll(parallelset);
		allrelationset.addAll(norelset);
		allrelationset.addAll(selfcycledirectset);
		allrelationset.addAll(selfcycleset);

		Iterator<String> iter = allelementset.iterator();
		Iterator<String> iter2 = allelementset.iterator();
		while (iter2.hasNext()) {
			allset.add(StoTmap.get(iter2.next()));
		}
		allrelationset.addAll(norelset);

		System.out.println("直接因果跟随关系：" + directcasualset);
		System.out.println("并发关系：" + parallelset);
		System.out.println("无关：" + norelset);
		System.out.println("allelement：" + allelementset);
		System.out.println("directfollowset" + directfollowset);

		int l = 0;
		List indexlist = new ArrayList();

		System.out.println(indexlist);

		int fg1 = 0, fg2 = 0;
		ao: for (int i = 0; i < allelementarr.length; i++) {
			int fg0 = logtotal[i] - mylog.size();
			System.out.println("fg0" + fg0);
			for (int j = 0; j < allelementarr.length; j++) {
				if (fg0 == logtotal[j]) {
					fg1 = 1;
					i = allelementarr.length + 3;
					break ao;
				}
			}
		}
		System.out.println("fg1" + fg1);
		ko: for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < allelementarr.length; j++) {
				for (int k = 0; k < allelementarr.length; k++) {
					if (tracetotal[i][j] - tracetotal[i][k] == 1) {
						fg2 = 1;
						break ko;
					}

				}
			}
		}
		System.out.println("fg2" + fg2);

	}

	private void swapArr(String[] arr, int mm, int nn) {
		// TODO Auto-generated method stub

		String xx = arr[mm];
		String yy = arr[nn];
		arr[mm] = yy;
		arr[nn] = xx;

	}

	private String fdcycle(org.processmining.Gather.Tuple temptuple, org.processmining.Gather.Tuple endtetuple) {
		// TODO Auto-generated method stub
		return null;
	}

	private Tuple Tuple(String flag, int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	private void createMatrix() {
		ArrayList<Triad> alltriadlist = new ArrayList<Triad>(allrelationset);
		for (int i = 0; i < alltriadlist.size(); i++) {
			Triad triad = alltriadlist.get(i);
			Transition first = triad.getFirst();
			Transition second = triad.getSecond();
			Relation relation = triad.getRelation();
			int f = TtoImap.get(first);
			int s = TtoImap.get(second);
			MatrixofRelation[f][s] = relation;
			MatrixofRelation[s][f] = relation.getReRelation();
		}
	}

	public Set<Relation> getRowRelation(Transition t) {
		Set<Relation> set = new HashSet<Relation>();
		int len = TtoImap.get(t);
		for (int i = 0; i < MatrixofRelation.length; i++) {
			set.add(MatrixofRelation[len][i]);
		}
		return set;
	}

	public Set<Relation> getColumnRelation(Transition t) {
		Set<Relation> set = new HashSet<Relation>();
		int len = TtoImap.get(t);
		for (int i = 0; i < MatrixofRelation.length; i++) {
			set.add(MatrixofRelation[i][len]);
		}
		return set;
	}

	public void changeInCasualRelation(Set<Triad> s) {
		ArrayList<Triad> tl = new ArrayList<Triad>(s);
		for (int i = 0; i < tl.size(); i++) {
			Triad triad = tl.get(i);
			System.out.println(triad);
			Transition t1 = triad.getFirst();
			Transition t2 = triad.getSecond();
			int t1pos = TtoImap.get(t1);
			int t2pos = TtoImap.get(t2);
			changeRelation(t1pos, t2pos, Relation.DirectCasually);
		}
		reSetRelation();
	}

	private void changeRelation(int i, int j, Relation r) {
		MatrixofRelation[i][j] = r;
		MatrixofRelation[j][i] = r.getReRelation();
	}

	private void reSetRelation() {
		directcasualset.clear();
		directcasualset.addAll(getNewRelation(Relation.DirectCasually));
		//indirectcasualset.clear();
		//indirectcasualset.addAll(getNewRelation(Relation.InDirectCasually));

		norelset.clear();
		norelset.addAll(getNewRelation(Relation.NoRel));
	}

	private Set<Triad> getNewRelation(Relation r) {
		Set<Triad> set = new HashSet<Triad>();
		for (int i = 0; i < MatrixofRelation.length; i++) {
			for (int j = 0; j < MatrixofRelation.length; j++) {
				Relation rel = MatrixofRelation[i][j];
				if (rel.equals(r)) {
					Transition t1 = ItoTmap.get(i);
					Transition t2 = ItoTmap.get(j);
					set.add(new Triad(new Tuple(t1, t2), r));
				}
			}
		}
		return set;
	}

	public Set<Triad> getDirectFollowSet() {

		return directfollowset;
	}

	public Set<Triad> getselfcycledirectset() {
		return selfcycledirectset;
	}

	//  public Set<Triad> getbpset() {

	//	  return bpset; }
	public Set<Triad> getparallelset() {

		return parallelset;
	}

	public Set<String> getparallelset1() {
		return parallset;
	}

	public Set<Triad> getDirectCasualSet() {
		return directcasualset;
	}

	public Set<String> getchoicenameset() {
		return choicenameset;
	}

	public Set<Triad> getNoRelSet() {
		return norelset;
	}

	public Map<String, Transition> getStoTmap() {
		return StoTmap;
	}

	public Map<Transition, Integer> getTtoImap() {
		return TtoImap;
	}

	public Map<Integer, Transition> getItoTmap() {
		return ItoTmap;
	}

	public Relation[][] getMatrixofRelation() {
		return MatrixofRelation;
	}

	public FindRely[] gettransrely() {
		return transrely;
	}

	public double[] getconfidencerely() {
		return confidencerely;

	}

	public void ShowMatrix() {
		for (int i = 1; i < MatrixtoString.length; i++) {//越界问题i-1
			MatrixtoString[0][i] = ItoTmap.get(i - 1).toString();
			MatrixtoString[i][0] = ItoTmap.get(i - 1).toString();
		}
		for (int i = 0; i < MatrixofRelation.length; i++) {
			for (int j = 0; j < MatrixofRelation.length; j++) {
				MatrixtoString[i + 1][j + 1] = MatrixofRelation[i][j].toString();
			}
		}
		for (int i = 0; i < MatrixtoString.length; i++) {
			for (int j = 0; j < MatrixtoString.length; j++) {
				System.out.printf("%4s", MatrixtoString[i][j]);
			}
			System.out.println();
		}
	}

	public Transition getFirstTransition() {
		Transition t = StoTmap.get(mylog.getFirstTrace().getFirstEvent().getName());
		return t;

	}

	public Transition getLastTransition() {
		Transition t = StoTmap.get(mylog.getFirstTrace().getLastEvent().getName());
		return t;
	}

	/*
	 * 输出二维数组方法
	 * 
	 */
	public static void printArray2(Relation[][] matrixofRelation2) {
		for (int x = 0; x < matrixofRelation2.length; x++) {
			for (int y = 0; y < matrixofRelation2[x].length; y++) {
				System.out.print(matrixofRelation2[x][y] + " ");
			}
			System.out.println();
		}
	}

}
