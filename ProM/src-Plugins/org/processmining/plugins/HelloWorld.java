package org.processmining.plugins;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.Data.MyEvent;
import org.processmining.Data.MyLog;
import org.processmining.FootMatrix.FootMatrix;
import org.processmining.Gather.Triad;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
public class HelloWorld {
        @Plugin(
                name = "AlphaID", 
                parameterLabels = {"Xlog"}, 
                		returnLabels = { "PetrinetAlphaID" },
            			returnTypes = { Petrinet.class },
                userAccessible = true
        )
        @UITopiaVariant(
                affiliation = "SDUST", 
                author = "Hongwei Sun", 
                email = "sunhongwei_doctor@yeah.net"
        )
        public Petrinet doMining(PluginContext context,XLog log)
    	{
            String label = "PetriNet";
    		XLog xlog1;
    		PetrinetImpl pnimpl = new PetrinetImpl(label);
    		//日志转换,获取变迁名
    		MyLog mylog = new MyLog(log);
    		
    		//初始化T
    		InitialTransition(mylog, pnimpl);
    		//生成足迹矩阵
    		FootMatrix footmatrix = CreateRightFootMatrix(mylog, pnimpl);
    		footmatrix.ShowMatrix();
         	CreatePetrinet(pnimpl, footmatrix);
    		Set<String> choicenameset= new HashSet<String>();
    		choicenameset.addAll(footmatrix.getchoicenameset());
    		
    	
    		return pnimpl;
    		
    		
    	}
        private  void InitialTransition(MyLog mylog,PetrinetImpl pnimpl)
    	{
    		//得到所有的事件
    		ArrayList<MyEvent> myeventlist = new ArrayList<MyEvent>(mylog.getMyEventSet());
    		//初始化网
    				for(int i = 0; i < myeventlist.size() ; i ++)
    				{
    					MyEvent myevent = myeventlist.get(i);
    					String name = myevent.getName();
    					pnimpl.addTransition(name);
    				}
    	}
    	
    	private void CreatePetrinet(PetrinetImpl pnimpl,FootMatrix footmatrix)
    	{
    		ArrayList<Triad> casuallist = new ArrayList<Triad>(footmatrix.getDirectCasualSet());
    		ArrayList<Triad> casuallist1 = new ArrayList<Triad>(footmatrix.getDirectCasualSet());
    		ArrayList<Triad> selfcycledirectlist = new ArrayList<Triad>(footmatrix.getselfcycledirectset());
    		ArrayList<Triad> parallellist = new ArrayList<Triad>(footmatrix.getparallelset());
    		Set<String> paralnameset= new HashSet<String>();
    		paralnameset.addAll(footmatrix.getparallelset1());
    		
    		
    		ArrayList<Place> placelist = new ArrayList<Place>();
    		placelist.add(pnimpl.addPlace("Start"));
    		pnimpl.addArc(placelist.get(0), footmatrix.getFirstTransition());
    	    while(casuallist.size()>0)
    	    {
    	    	Triad triad = casuallist.remove(0);
    	    	int i=0,l=0;
    	    	Transition firstt = triad.getFirst();
    	    	Transition secondt = triad.getSecond();
    	    	
    	    	
    	    	Place place = pnimpl.addPlace("p"+i++);
    	    	int flag1=0,flag2=0,flag3=0,flag4=0;
    	//并发    	
    	    /*	Triad triadpall = parallellist.remove(0);
    	    	
    	    	Transition firstt1 = triadpall.getFirst();
    	    	Transition secondt1 = triadpall.getSecond();
    	    	Place place1 = pnimpl.addPlace("p*"+l++);
    	    //	int flag1=0,flag2=0,flag3=0,flag4=0;
    	    	*/
    	    	
    	    	//并发结构
    	    	//// int m=0;
     	    	///for( m=0;m<parallellist.size();m++) {
         	 		
     	    	////	Triad tempbp = parallellist.get(m);
     	    
     	    	
    	    	
    	    	
     	    	
    	    	
    	    	
    	    	//因果关系中的选择结构
    	    	for(int j=0;j<casuallist.size();j++)
    	    	{
    	    		Triad tempcasuall = casuallist.get(j);
    	    	
    	    		//Triad tempcasuall = casuallist1.remove(0);
    	    		//if(firstt.equals(tempcasuall.getFirst())&&!secondt.equals(tempcasuall.getSecond())&&!parallellist.toString().contains((CharSequence) secondt))
    	    		if(firstt.equals(tempcasuall.getFirst())&&!secondt.equals(tempcasuall.getSecond())&& !paralnameset.contains(secondt.toString()))//
    	    		{
    	    			flag1=1;
      	    			Transition bpend=tempcasuall.getSecond();
    	    			
    	    	    	//placelist.add(place);
    	    			
    	            	casuallist.remove(tempcasuall);
    	            	
    	    	    	j--;
    	    	    	pnimpl.addArc(place, bpend);
    	    		}
    	    		//if(secondt.equals(tempcasuall.getSecond())&&!firstt.equals(tempcasuall.getFirst())&&!parallellist.toString().contains((CharSequence) firstt))
    	    		if(secondt.equals(tempcasuall.getSecond())&&!firstt.equals(tempcasuall.getFirst())&&!paralnameset.contains(firstt.toString()))
    	    		{
    	    			flag2=1;
    	    			Transition start=tempcasuall.getFirst();
    	    			
    	    	    	//placelist.add(place);
    	            casuallist.remove(tempcasuall);
    	           	j--;
    	    	    	pnimpl.addArc(start, place);
    	    	    
    	    		}
    	    
    	    /*if (paralnameset.contains(firstt.toString())&&!paralnameset.contains(secondt.toString())) {
    	       		   
    	     		     flag4=1;
    	     			Transition bpend=tempcasuall.getSecond();

    	 	    		Place place2 = pnimpl.addPlace("p*"+ l++);
    	 	    		casuallist.remove(tempcasuall);
    	 	    		j--;
    	 	    		pnimpl.addArc(firstt, place2);
    	 	    		pnimpl.addArc(place2, bpend);
    	     		  
    	 		} 	
    	      	    	
    	     	   if (paralnameset.contains(secondt.toString())&&!paralnameset.contains(firstt.toString())) {
    	     		   
    	   		     flag4=1;
    	   		  Transition start=tempcasuall.getFirst();
    	 	    		Place place2 = pnimpl.addPlace("p#"+ l++);
    	 	    		casuallist.remove(tempcasuall);
    	 	    		j--;
    	 	    		pnimpl.addArc(firstt, place2);
    	 	    		pnimpl.addArc(place2, start);
    	   		  
    	 		} 	   	
    	      	    	
    	    	/*	if ((secondt.equals(parallellist.get(j).getFirst()))||(secondt.equals(parallellist.get(j).getSecond()))) 
    	    		{
        	    		flag3=1;
        	    		Place place1 = pnimpl.addPlace("p*"+ l++);
        	    		
        	    		j--;
        	    		//m--;
        	    		pnimpl.addArc(firstt, place1);
        	    		pnimpl.addArc(place1, secondt);
        	    		casuallist.remove(tempcasuall);
        	    		//parallellist.remove(tempbp);
    				}	
        	    		
        	    	if((firstt.equals(parallellist.get(j).getFirst()))||(firstt.equals(parallellist.get(j).getSecond()))) 
        	    	{
        	    		flag4=1;
        	    		Place place2 = pnimpl.addPlace("p#"+ l++);
        	    		
        	    		//m--;
        	    		j--;
        	    		pnimpl.addArc(firstt, place2);
        	    		pnimpl.addArc(place2, secondt);
        	    	
    	    		
    	    			casuallist.remove(tempcasuall);
    				}	*/
    	    		
    	    		
    	    		
    	    	//pnimpl.addArc(firstt, place);
    	    	//pnimpl.addArc(place, secondt);
    	    	//Place place = pnimpl.addPlace("p"+i);
    	    	//placelist.add(place);
    	    	/*	for(int m=0;m<parallellist.size();m++) {
            	 		
        	    		Triad tempbp = parallellist.get(m);
        	    		
        	    	if ((secondt.equals(tempbp.getFirst()))||(secondt.equals(tempbp.getSecond()))) {
        	    		flag3=1;
        	    		Place place1 = pnimpl.addPlace("p*"+ l++);
        	    		
        	    		
        	    		//m--;
        	    		pnimpl.addArc(firstt, place1);
        	    		pnimpl.addArc(place1, secondt);
        	    		casuallist.remove(triad);
        	    		//parallellist.remove(tempbp);
    				}	
        	    		
        	    	if((firstt.equals(tempbp.getFirst()))||(firstt.equals(tempbp.getSecond()))) {
        	    		flag4=1;
        	    		Place place2 = pnimpl.addPlace("p#"+ l++);
        	    		casuallist.remove(triad);
        	    		//m--;
        	    		pnimpl.addArc(firstt, place2);
        	    		pnimpl.addArc(place2, secondt);
    				}	
        	    	
        	    	}*/
        	   	
    	    	}

    	    	   
    	    	   
    	    	   
    	    	   
    	    	   
				
			
    	    	
    	    	
    	    	for(int k=0;k<selfcycledirectlist.size();k++)
    	    	{
    	    		Triad selfcyclesirecttriad = selfcycledirectlist.get(k);
    	    		if(triad.getFirst().equals(selfcyclesirecttriad.getFirst()))
    	    		{
    	    			pnimpl.addArc(place, selfcyclesirecttriad.getSecond());
    	    		}
    	    		if(triad.getSecond().equals(selfcyclesirecttriad.getSecond()))
    	    		{
    	    			pnimpl.addArc(selfcyclesirecttriad.getFirst(),place);
    	    		}
    	    	
    	    	}
    	    	
    	    	
    	        
 	    	    
    	    	
//    	    	if(flag1==1&&flag2==1)
//    	    	{
//    	    		flag1=0;
//    	    		flag2=0;
//    	    	 	pnimpl.addArc(firstt, place);
// 	    	        pnimpl.addArc(place, secondt);  		
//    	    	}
    	    	//else{
    	    	
//	        	pnimpl.addArc(firstt, place);
//    	    	pnimpl.addArc(place, secondt);
    	    	// casuallist.remove(triad);
    	    //	}
    	    	
    	    	
    	    	pnimpl.addArc(firstt, place);
 	    	    pnimpl.addArc(place, secondt);
    	    }
    	   
    	    placelist.add(pnimpl.addPlace("End"));
    	    pnimpl.addArc(footmatrix.getLastTransition(),placelist.get(placelist.size()-1));
    		
    	}
    	
    	private  FootMatrix CreateRightFootMatrix(MyLog mylog,PetrinetImpl pnimpl)
    	{
    		//先生成足迹矩阵
    		FootMatrix footmatrix = new FootMatrix(mylog, pnimpl);
    		footmatrix.ShowMatrix();
    		return footmatrix;
    	}
    }