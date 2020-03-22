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
	private Set<Triad> bpset;//��Ӳ��м�
	private Set<Triad> parallelset;
	public Set<String> allelementset = new HashSet<String>();//ȡ������Ԫ��
	public Set<String> copycycleset = new HashSet<String>();//ѭ��Ԫ�ظ���
	public Set<String> cycleset = new HashSet<String>();//ȡ������ѭ��Ԫ��
	public Set<String> choicenameset = new HashSet<String>();
	public Set<String> cyclenameset = new HashSet<String>();//��ȡ������ѭ����������
	public Set<String> cyclenameset1 = new HashSet<String>();//��ȡ������ѭ����������
	public Set<String> choiceset = new HashSet<String>();//ȡ������ѡ��Ԫ��
	public Set<String> parallset = new HashSet<String>();//ȡ�����в���Ԫ��

	public Set<String> traceelementset = new HashSet<String>();//ȡ�����л����
	public Set<String> traceelementsetall = new HashSet<String>();//ȡ�����л����
	public Set<String> cychset = new HashSet<String>();//ѭ��ѡ������
	public Set allset = new HashSet();
	public Set<String> copycyclenameset = new HashSet<String>();//��ȡ������ѭ����������

	public Set<String> ceclestrset = new HashSet<String>();//����Ի��Ļ��

	public Set<Tuple> dfset = new HashSet<Tuple>();//ֱ�Ӹ��漯��
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
		allrelationset = new HashSet<Triad>();//���й�ϵ��
		directfollowset = new HashSet<Triad>();//ֱ�Ӹ����ϵ��
		//indirectfollowset = new HashSet<Triad>();
		directcasualset = new HashSet<Triad>();//ֱ�������ϵ��
		backpacksset = new HashSet<Triad>();//��·��
		//indirectcasualset = new HashSet<Triad>();
		parallelset = new HashSet<Triad>();//���м�
		norelset = new HashSet<Triad>();// ����Ľڵ㼯
		selfcycleset = new HashSet<Triad>();//�Ի���
		selfcycledirectset = new HashSet<Triad>();//�Ի�ֱ�Ӹ��漯
		//bpset= new HashSet<Triad>();
		int size = StoTmap.size();//��Ǩ�����ʼ����С
		MatrixofRelation = new Relation[size][size];
		MatrixtoString = new String[size + 1][size + 1];
		for (int i = 0; i < MatrixofRelation.length; i++) {
			for (int j = 0; j < MatrixofRelation.length; j++) {
				MatrixofRelation[i][j] = Relation.NoRel;

			}
		}
		MatrixtoString[0][0] = " ";

		//ѭ�������ʼ������ӣ�

		for (int i = 0; i < MatrixtoString.length; i++) {

			for (int j = 0; j < MatrixtoString.length; j++) {
				System.out.print(MatrixtoString[i][j] + " ");
			}
			System.out.println();
		}

		printArray2(MatrixofRelation);

	}

	private void createBaseRelation() {
		//������еļ�
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
			System.out.println("��[" + i + "]" + tracestr1111[i]);
		}

		//	Set<Tuple> dfset = new HashSet<Tuple>();//ֱ�Ӹ��漯��
		//Set<Tuple> idfset = new HashSet<Tuple>();
		for (int i = 0; i < mylog.size(); i++) {
			MyTrace trace = mylog.get(i); //��־�еļ�
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

		Iterator<Tuple> dirit = dfset.iterator();//���õ��������������顣
		while (dirit.hasNext()) {
			Tuple t = (Tuple) dirit.next();
			Triad triad = new Triad(t, Relation.DirectFollow);
			directfollowset.add(triad);
		}
		System.out.println("ֱ�Ӹ����ϵ:" + directfollowset);
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
		//ѭ��������е�ֱ�Ӹ����Ԫ�飨�����	
		System.out.println("ֱ�Ӹ����Ԫ��1:" + alltuple1);

		ArrayList<Tuple> alltuplelistm = new ArrayList<Tuple>(alltuple);

		for (int i = 0; i < alltuplelistm.size(); i++) {

			System.out.println("ֱ�Ӹ����Ԫ��:" + alltuple);

		}

		//����parallel��ϵ
		Set<Tuple> parttuple = new HashSet<Tuple>();
		Set<Tuple> parttuple1 = new HashSet<Tuple>();
		Set<Tuple> paralleltuple = new HashSet<Tuple>();//���л��
		Tuple tuple = null;
		Tuple retuple = null;
		ArrayList<Tuple> alltuplelist = new ArrayList<Tuple>(alltuple);
		ArrayList<Tuple> alltuplelist1 = new ArrayList<Tuple>(alltuple);
		for (Tuple a : alltuplelist) {
			System.out.println("tuplelisttest:" + a);
		}
		while (alltuplelist.size() > 0) {

			tuple = alltuplelist.remove(0);

			allelementset.add(tuple.getFirst().toString());//��Ԫ����ӵ����м�����
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
				parttuple.add(tuple);//����������ֱ�Ӹ����Ԫ��
				parttuple1.add(tuple);//����������ֱ�Ӹ����Ԫ��
				System.out.println("tuple1." + tuple.getFirst().toString());
			}
		}

		/* ������Ԫ�ؼ���ת��Ϊ������� */
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

		//ѭ�����tracestr������־
		for (int i = 0; i < mylog.size(); i++) {
			System.out.println("tracestr[" + i + "]" + tracestr[i]);
		}

		int[] logtotal = new int[allelementarr.length];//��־Ԫ���ܼƾ���
		int[][] tracetotal = new int[mylog.size()][allelementarr.length];//�켣Ԫ�ؼ�������
		for (int i = 0; i < allelementarr.length; i++) {
			logtotal[i] = 0;
			System.out.print(logtotal[i] + " ");
		} //��ʼ����־�ͳ�ƾ���

		System.out.println();

		for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < allelementarr.length; j++) {
				tracetotal[i][j] = 0;
				System.out.print(tracetotal[i][j] + " ");
			}
			System.out.println();
		} //��ʼ�����ͳ�ƾ���

		//������м�Ԫ��
		for (int i = 0; i < mylog.size(); i++) {
			MyTrace testtrace = mylog.get(i);
			for (int j = 0; j < testtrace.size(); j++) {
				MyEvent testeventtotalall = testtrace.get(j);
				traceelementsetall.add(testeventtotalall.toString());
				//System.out.print("��"+i + testtrace.get(j).toString());
			}
			System.out.print("��Ԫ��" + i + traceelementsetall.toString());

			System.out.println();
		}

		//�õ�ѡ����ѭ���

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
						//�ж��ǲ���ѭ����
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

		//ͳ�Ƽ��еĻ���ִ�������ӣ�
		for (int i = 0; i < allelementarr.length; i++) {
			for (int j = 0; j < mylog.size(); j++) {
				logtotal[i] += tracetotal[j][i];

			}
			System.out.print("���л������" + allelementarr[i] + logtotal[i]);
		}
		System.out.println();

		//ͳ����־����ִ���,����Ĵ���С����־���ܳ��ȣ���ô�����п�����ѡ���������п�����ѭ�����
		for (int i = 0; i < allelementarr.length; i++) {
			if (logtotal[i] < mylog.size()) {
				choiceset.add(allelementarr[i]);
			}
			System.out.printf("%d  ", logtotal[i]);

		}
		//��ѡ��ṹ��ȥ��ѭ�����ʣ��ѡ��ṹ�
		choiceset.removeAll(cycleset);
		System.out.println("choicesetfinal" + choiceset);
		System.out.println("*******logtotal********,\n");
		//�����Ǩ��Ŀ����
		for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < allelementarr.length; j++) {
				System.out.printf("%d ", tracetotal[i][j]);
			}
			System.out.println("\n");
		}
		/* ��ӻ�ȡ��ѭ����ͻص���������(tianjia ) */
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
		 * } j--; System.out.println("ѭ����ʼ���:" + loopstartset);
		 * 
		 * }
		 */

		//}

		// ����ͬ�����ѭ����ʼ�������ӣ�
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
				System.out.println("ѭ����ʼ���:" + loopstartset);

			}

		}
		ArrayList<Tuple> alltuplelist3 = new ArrayList<Tuple>(alltuple);
		//����  ���ѭ������� �����

		Tuple tuple2 = null;
		for (int j = 0; j < alltuplelist3.size(); j++) {

			tuple2 = alltuplelist3.remove(j);
			Transition s = tuple2.getSecond();
			Transition f = tuple2.getFirst();
			if (cycleset.contains(f.toString()) && loopstartset.contains(s.toString())) {

				loopendtset.add(f.toString());

			}
			j--;
			System.out.println("ѭ���������:" + loopendtset);

		}

		ArrayList<Tuple> alltuplelist4 = new ArrayList<Tuple>(alltuple);
		//ƥ�䣬ѭ���еĿ�ʼ��ͽ���� ����ӣ�
		Set<String> ceclestrset1 = new HashSet<String>();//�������ѭ�����      
		Set<String> ceclestrset0 = new HashSet<String>();//���ѭ�����      
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
			System.out.println("ѭ���ṹ���:" + ceclestrset1);
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
		 * System.out.println("ѭ���ṹ��ʼ��ͽ����11:" + cyclestartendarr1[j]);
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * System.out.println("ѭ���ṹ��ʼ��ͽ����:" + cyclestartendarr[i]); }
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
		 * System.out.println("ѭ���ṹ��ʼ��ͽ����:" + cyclestartendarr1[i]);
		 * 
		 * }
		 */

		//���� ȡ��ѭ���е����������ϵ�����0
		//  String[] cycledrietsettoarr = new String[parttuple1.size()];
		// cycledrietset.add(parttuple1.toString());
		// cycledrietset.toArray(cycledrietsettoarr);

		ArrayList<Tuple> cycledrietsetlist = new ArrayList<Tuple>(parttuple1);
		Set<Tuple> cycledrietset = new HashSet<Tuple>();//ѭ���ṹ�е�ֱ���������
		for (int i = 0; i < cycledrietsetlist.size(); i++) {

			Tuple t = cycledrietsetlist.remove(i);
			Transition s = t.getSecond();
			Transition f = t.getFirst();
			if (cycleset.contains(s.toString()) && cycleset.contains(f.toString())) {

				cycledrietset.add(t);

			}
			i--;
		}

		System.out.println("ѭ���ṹ��ֱ�������ϵ:" + cycledrietset);

		// ArrayList<Tuple> cycledrietsetlist1 = new ArrayList <Tuple>(cycledrietset);  

		Tuple tuple4 = null;
		Tuple tuple5 = null;
		//ƥ�䣬ѭ���еĿ�ʼ��ͽ����
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

		//��ѡ���ѭ���������һ��
		cychset.addAll(choiceset);
		cychset.addAll(cycleset);
		cycleset.toArray(cyclesettoarr);
		//���ѭ�������
		for (int i = 0; i < cycleset.size(); i++) {
			System.out.println("new cyclesettoarr" + cyclesettoarr[i]);
		}

		//���
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
		 * System.out.println("ÿ��ѭ�����ַ���"+loopstrarr[i]);
		 * 
		 * 
		 * }
		 */
		//���ܣ����ѭ���ṹ����name����ӣ�

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
			System.out.println("ÿ��ѭ�����ַ���" + loopstrarr[i]);

		}

		for (int j = 0; j < loopstrarr.length; j++) {

			cyclenameset1.add(loopstrarr[j].toString());
			System.out.println("����ѭ�����Ƽ���" + cyclenameset1);
		}

		System.out.println("����ѭ�����Ƽ���" + cyclenameset1);

		//֤����������ѭ���еĻ�������ҵ�һ����·��
		Tuple temptuple = null;
		Tuple endtetuple = null;

		for (int i = 0; i < cyclesettoarr.length; i++) {
			String tempstr = cyclesettoarr[i];
			String findstr = null;
			int flag = 0;//�ҵ�һ����·��ֹͣ�ı��
			int flag1 = 0;
			System.out.println("tempstr:" + tempstr);
			System.out.println("alltuplelist1.size:" + alltuplelist1.size());

			//ȡ������ѭ���ṹ�Ķ�Ԫ��
			for (int j = 0; j < alltuplelist1.size() && flag == 0; j++) {
				System.out.println("�����һ��ѭ��");
				if (cyclesettoarr[i].equals(alltuplelist1.get(j).getFirst().toString())
						&& cychset.contains(alltuplelist1.get(j).getSecond().toString())) {
					temptuple = alltuplelist1.get(j);
					if (flag1 == 0) {
						tempstr = tempstr + alltuplelist1.get(j).getSecond().toString();//������ַ���������
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
			} //�ҵ��ö�Ԫ�顣
				//findstr=fdcycle(temptuple,endtetuple,alltuplelist1,tempstr);

			aa: for (int m = 0; m < alltuplelist1.size() && flag == 0; m++) {
				int flag2 = 0;
				for (int k = 0; k < alltuplelist1.size(); k++) {
					System.out.println("�����2��ѭ��");
					if (temptuple.getSecond().equals(temptuple.getFirst())) {//�����Ի���
						ceclestrset.add(tempstr);
						break aa;
					}
					if ((temptuple.getSecond().equals(endtetuple.getFirst())
							&& endtetuple.getSecond().equals(temptuple.getFirst()))) {
						tempstr = tempstr + endtetuple.getSecond().toString();
						ceclestrset.add(tempstr);//����ѭ��
						System.out.println("��ʱ�������");
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

		//�ҵ�һ��ѭ�����ҽ�������
		for (int i = 0; i < cyclesettoarr.length; i++)//ѭ�������
		{
			System.out.println("Ѱ��====" + cyclesettoarr[i]);
		}
		System.out.println("xunzhao++++" + copycycleset);
		//�ҵ�ѭ���ṹ��������������
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

		System.out.println("����ѭ�����Ƽ���" + cyclenameset1);

		//�ҵ�ѡ����������ѡ��ṹ
		Set<String> copychoiceset = new HashSet<String>();
		copychoiceset.addAll(choiceset);
		String[] choicesettoarr = new String[choiceset.size()];//�Ѽ��ϱ������
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
					System.out.println("һ��ʣ��" + copychoiceset);
				}
				if (alltuplelist1.get(k).getSecond().toString().equals(str1)
						&& copychoiceset.contains(alltuplelist1.get(k).getFirst().toString())) {
					copychoiceset.remove(alltuplelist1.get(k).getFirst().toString());
					str1 = alltuplelist1.get(k).getFirst().toString();
					str = str1 + str;
					i = i + 1;
					copychoiceset.remove(alltuplelist1.get(k).getFirst().toString());
					System.out.println("choicestr2+" + str);
					System.out.println("�ڶ���str1" + str1);
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
		/** �ҵ������е�ѡ��ṹ�����е�ѭ���ṹ*��һ����ȷ����������֮��ĵĹ�ϵ **/

		//��ѭ��������ת��Ϊ����,��ѡ��ṹ��׼��Ϊ����
		String[] choicenamesettoarr = new String[choicenameset.size()];//��ѡ��������ת��������
		choicenameset.toArray(choicenamesettoarr);
		String[] cyclenamesettoarr = new String[cyclenameset.size()];//��ѭ����ת��Ϊ����
		cyclenameset.toArray(cyclenamesettoarr);
		/* ͳ��ѭ���ṹ����Ŀ��ѭ�������Ŀ */
		int[] cyclemaxarr = new int[cyclenamesettoarr.length];//ת����ѭ������ĸ�������
		int[] cycleelementmax = new int[cycleset.size()];//ѭ����Ĵ����Ŀ������

		for (int i = 0; i < cyclemaxarr.length; i++) {//��������г�ʼ��
			cyclemaxarr[i] = 0;
			System.out.println(cyclemaxarr[i]);
		}
		for (int i = 0; i < cycleelementmax.length; i++) {
			cycleelementmax[i] = 0;
		}

		//��� ���� ʵ�ֶ�ѭ�����������ѭ���ṹ����ͳ��	

		// ��ѡ��ṹ��ӽ�ȥ �ҵ�����֮��Ĺ�ϵ��������ӣ�

		Set<String> loopchset = new HashSet<String>();//ѭ��ѡ�ۻ��	

		loopchset.addAll(loopendtset);

		////	loopchset.addAll(choiceset);

		loopchset.addAll(choicenameset);

		////String[] loopchoicetoarr = new String[choiceset.size()+loopendtset.size()];
		String[] loopchoicetoarr = new String[choicenameset.size() + loopendtset.size()];

		loopchset.toArray(loopchoicetoarr);

		int[][] loopstructtotal = new int[mylog.size()][loopendtsetarr.length];//ѭ���ṹ����ͳ�ƾ���	

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

		// ��ѡ��ṹ��ӽ�ȥ �ҵ�����֮��Ĺ�ϵ��������ӣ� 

		int[][] loopchoicestructtotal = new int[mylog.size()][loopendtsetarr.length + choicenamesettoarr.length];//ѭ���ṹ����ͳ�ƾ���	

		for (int i = 0; i < mylog.size(); i++) {

			for (int j = 0; j < loopendtsetarr.length + choicenamesettoarr.length; j++) {

				loopchoicestructtotal[i][j] = 0;
				System.out.print(loopchoicestructtotal[i][j] + " ");
			}
			System.out.println();
		}
		//////////////////////////////////
		/////�������������Ϊ�ڷ��ֵ�ѭ���ṹ˳Ѱ�Ƿ��ģ����Ի������
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

		//��ͳ�Ƴ�ÿ������ѭ�����ֵĴ�����ѡ����ֵĴ�(���)

		for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < loopchoicetoarr.length; j++) {

				String lcc = loopchoicetoarr[j];

				System.out.print(lcc + loopchoicestructtotal[i][j] + " ");
			}

			System.out.println();
		}

		//�ٴ�ȷ��ѭ������еĻ�ǲ�������Щ���г��ֶ�Σ�ͳ�Ƴ�ѭ�����ͬһ�����г��ֵ�������	

		for (int i = 0; i < cyclesettoarr.length; i++) {
			String temp = cyclesettoarr[i];
			int max = 0;
			for (int j = 0; j < mylog.size(); j++) {
				int fromIndex = 0;
				int count = 0;
				while (true) {
					int index = tracestr[j].indexOf(temp, fromIndex);//���ش� fromIndex λ�ÿ�ʼ����ָ���ַ����ַ����е�һ�γ��ִ���������������ַ�����û���������ַ����򷵻� -1��
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
			System.out.println("ѭ��Ԫ������" + cycleelementmax[i]);
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
			System.out.println("���" + cyclemaxarr[i]);
		}
		/*
		 * cyclenameset.clear(); for (int i = 0; i < cyclenamesettoarr.length;
		 * i++) { cyclenameset.add(cyclenamesettoarr[i] + ":0"); } for (int i =
		 * 0; i < cyclenamesettoarr.length; i++) {
		 * //System.out.println(cyclemaxarr[i]); for (int j = 0; j <
		 * cyclemaxarr[i]; j++) { cyclenameset.add(cyclenamesettoarr[i] + ":" +
		 * (j + 1)); } } System.out.println("����ѭ��������ѭ��������" + cyclenameset);
		 */

		System.out.println("����ѭ�����Ƽ���" + cyclenameset1);

		//loopstrarr ѭ��������

		int total = 0;
		//�з�trace
		//ѭ��
		String[] cyclenumbersettoarr = new String[cyclenameset1.size()];
		cyclenameset1.toArray(cyclenumbersettoarr);
		total = cyclenameset1.size();
		Set<FindRely> rely = new HashSet();
		ArrayList<FindRely> rely11 = new ArrayList<>();

		Set<FindRely> copyrely = new HashSet();

		for (int i = 0; i < cyclenameset1.size(); i++) {
			System.out.println("cyclenumbersettoarr+" + i + cyclenumbersettoarr[i]);
		}

		/////////////////////////////////////Ѱ��ѭ���ľͽ�ѡ������(zhuyi)
		System.out.println("Ѱ��ѭ��Ӱ���ѡ��++++++++++++++++++++++++++");

		// �������ѭ��ʱ˳���Ƿ��ģ���ô���䷴������
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
		///	int index = tracestr[j].indexOf(temp, fromIndex);//���ش� fromIndex λ�ÿ�ʼ����ָ���ַ����ַ����е�һ�γ��ִ���������������ַ�����û���������ַ����򷵻� -1��
		//��������ѭ���Ĺ�ϵӰ�� ��ʱ ���
		/* ���� ����ط��ǵõ�ÿ�����е�ѭ����ѡ��Ӱ��ĺ�һ��ѭ���Ĵ��� */
		for (int i = 0; i < loopstrarr.length - 1; i++) {

			System.out.println("�����1��");
			for (int j = 0; j < tracestr.length; j++) {

				FindRely re = new FindRely();//ʵ����һ����ϵ��������

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
				System.out.println("�����2��");
				String str = loopstrarr[i];
				String[] cut = tracestr[j]
						.split(/* "("+ */loopstrarr[i]/* +")+" */);//��ѭ��ȥ�и�
				String[] cut1 = tracestr[j]
						.split(/* "("+ */loopstrarr[i]/* +")+" */);//��ѭ��ȥ�и�
				int pos = 0;

				for (int s = 1; s < cut.length; s++) {
					if (!cut[s].isEmpty()) {

						System.out.println("cut[" + s + "]:" + cut[s]);

						re.setStr(str);//����ֵ

						re.setNumber(cut.length - 1);//���ô�С������ѭ������Ŀѭ��������1
						System.out.println("......" + re.toString());

						for (int r = 0; r < choicenamesettoarr.length; r++) {
							String[] cutnext0 = null;
							if (cut.length < 2) {
								System.out.println("������");
								continue;
							} else

							//���ﲻӦ����CUT1
							{
								if (cut[cut.length - 1].contains(choicenamesettoarr[r])) {
									System.out.println("����");
									cutnext0 = cut1[cut.length - 1].split(choicenamesettoarr[r]);//��ѡ������ѭ��ȥ�и�	

									re.setStr0(String.valueOf(choicenamesettoarr[r]));
									re.setNumber0(1);//���ô�С��

									System.out.println("......" + re.toString());
									//	break;

									for (int k = 1; k < loopstrarr.length; k++) {
										if (cutnext0[1].contains(loopstrarr[k])) {
											String[] cutnext = cutnext0[1].split(loopstrarr[i + 1]);//�ú�ߵ�ѭ��ѭ��ȥ�и�

											for (int l = 0; l < cutnext.length; l++) {
												System.out.println("�ڶ����и�ĵ�ֵ" + cutnext[l]);
											}
											if (cutnext.length < 2) {
												System.out.println("������");
												continue;
											} else {
												re.setNumber1(cutnext.length - 1);
												re.setFlow1(String.valueOf(loopstrarr[i + 1]));//��Ӱ���ѭ��

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

				System.out.println("��������");
				rely.add(re);
				rely11.add(re);
				System.out.println("��������11" + rely);
				System.out.println("��������rely11" + rely11);

			}
		}
		/*
		 * ����ط�ֻ���ʺ�ѡ��ṹ�к���һ������������ѡ���֧���б�Ǩ������ô�����ϱߵĴ���
		 * 
		 * for (int m = 0; m < cut[s].length(); m++) {
		 * System.out.println("cut[s].substring(m)" + cut[s].substring(m, m +
		 * 1));//s����Ӽ� if (choicenameset.contains(cut[s].substring(m, m + 1))) {
		 * //System.out.println("p" + p); System.out.println("����");
		 * re.setStr0(String.valueOf(cut[s].charAt(m)));
		 * re.setNumber0(1);//���ô�С��
		 * //re.addFlow(String.valueOf(cut[s].charAt(m)));
		 * System.out.println("......" + re.toString()); break; } else {
		 * System.out.println("������"); } //}
		 * 
		 * 
		 * //��������ط��ǲ���Ӧ�ð��±߰�����ȥ
		 */

		//���ѭ���ṹ ��ߵ�ѭ���ṹ					

		/*
		 * for (int k = 1; k < loopstrarr.length; k++) {
		 * 
		 * String[] cutnext = cutnext0[1].split(loopstrarr[i+1]);//�ú�ߵ�ѭ��ѭ��ȥ�и�
		 * 
		 * for (int l = 0; l < cutnext.length; l++) {
		 * System.out.println("�ڶ����и�ĵ�ֵ"+cutnext[l]); } if (cutnext.length<2){
		 * System.out.println("������"); continue; } else {
		 * re.setNumber1(cutnext.length-1);
		 * re.setFlow1(String.valueOf(loopstrarr[i+1]));//��Ӱ���ѭ��
		 * 
		 * System.out.println("......" + re.toString()); // rely.add(re); break;
		 * }
		 * 
		 * 
		 * }
		 */

		//ͳ��ѭ����ѡ��Ӱ��ѭ����������Ŀ		����ӣ�
		//ѭ����ѡ��Ӱ��ѭ��

		FindRely[] relysettoarr = new FindRely[rely.size()];
		rely.toArray(relysettoarr);

		int relysetcount[] = new int[rely.size()]; //��������
		for (int k = 0; k < rely.size(); k++) {
			relysetcount[k] = 0;
			System.out.println("������Ŀrelysetcount" + k + relysetcount[k]);

		}

		FindRely[] loopchoicerelylooptoarr1 = new FindRely[rely11.size()];
		rely11.toArray(loopchoicerelylooptoarr1);
		int loopchoicerelyloopcount[] = new int[rely.size()]; //��������
		for (int k = 0; k < rely.size(); k++) {
			loopchoicerelyloopcount[k] = 0;
			System.out.println("������Ŀloopchoicelooprelycount" + k + loopchoicerelyloopcount[k]);

		}

		for (int i = 0; i < relysettoarr.length; i++) {
			System.out.println("��һ��ѭ��0000000");
			for (int j = 0; j < loopchoicerelylooptoarr1.length; j++) {
				System.out.println("�ڶ���ѭ��0000000");
				if (relysettoarr[i].equals(loopchoicerelylooptoarr1[j])) {

					loopchoicerelyloopcount[i]++;

				}

			}

		}

		//����������

		supportrely = new double[relysettoarr.length];
		confidencerely = new double[relysettoarr.length];
		transrely = new FindRely[rely.size()];//�½�һ������
		System.arraycopy(relysettoarr, 0, transrely, 0, relysettoarr.length);//��һ�����鸴�Ƶ���һ����������ȥ

		for (int i = 0; i < relysettoarr.length; i++) {
			System.out.println("transrely" + i + ":" + transrely[i]);
		}
		System.out.println("mylog.size" + mylog.size());
		//��һ������������־������Ҫ����
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
						&& relysettoarr[i].getNumber0() == relysettoarr[j].getNumber0()) //ѭ��+ѡ�� Ӱ��  ѭ��
				{
					count += loopchoicerelyloopcount[j];
				}
				//			if(relytoarr[i].getStr().equals(relytoarr[j].getStr())&&cyclenameset.contains(relytoarr[i].getStr())&&relytoarr[i].getNumber()==relytoarr[j].getNumber())//ѭ��Ӱ��������
				//				{��
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
				System.out.println("loop(" + relysettoarr[k].getStr() + "," + relysettoarr[k].getNumber() + ")" + "��"
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

		System.out.println("dirtuple" + dirtuple);//ֱ�Ӹ����Ԫ��
		System.out.println("parttuple" + parttuple);//��ȥ������ֱ�Ӹ����Ԫ�飬��ֱ�������ϵ��Ԫ��
		Set<Tuple> dircasualtuple = new HashSet<Tuple>(dirtuple);
		dircasualtuple.retainAll(parttuple);//��parttuple�е�Ԫ�ط���dircasualtuple��
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
		//�ǲ�����������?
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

		System.out.println("ֱ����������ϵ��" + directcasualset);
		System.out.println("������ϵ��" + parallelset);
		System.out.println("�޹أ�" + norelset);
		System.out.println("allelement��" + allelementset);
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
		for (int i = 1; i < MatrixtoString.length; i++) {//Խ������i-1
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
	 * �����ά���鷽��
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
