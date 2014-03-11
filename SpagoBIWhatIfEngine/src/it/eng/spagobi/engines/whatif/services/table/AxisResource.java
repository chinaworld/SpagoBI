package it.eng.spagobi.engines.whatif.services.table;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.common.AbstractWhatIfEngineService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.OlapConnection;
import org.olap4j.metadata.Hierarchy;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.mdx.MdxStatement;
import com.eyeq.pivot4j.query.QueryAdapter;
import com.eyeq.pivot4j.transform.PlaceHierarchiesOnAxes;
import com.eyeq.pivot4j.transform.SwapAxes;
import com.eyeq.pivot4j.transform.impl.PlaceHierarchiesOnAxesImpl;

@Path("/v1.0/axis")
public class AxisResource extends AbstractWhatIfEngineService {
	
	public static transient Logger logger = Logger.getLogger(AxisResource.class);
	
	/**
	 * Swap the axes
	 * @return
	 */
	@PUT
	@Path("/swap")
	public String swapAxis(){
		logger.debug("IN");
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		
		SwapAxes transform = model.getTransform(SwapAxes.class);
		if(transform.isSwapAxes()){
			transform.setSwapAxes(false);
		}else{
			transform.setSwapAxes(true);
		}
				
		String table = renderModel(model);
		logger.debug("OUT");
		return table;
		
	}
	
	@PUT
	@Path("/{axis}/removehierarchy/{hierarchy}")
	public String removeHierarchy(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("hierarchy") String hierarchyName){

		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		OlapConnection connection = ei.getOlapConnection();
		QueryAdapter qa = new QueryAdapter(model);
		qa.initialize();
		
		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, connection);
		
		CellSet cellSet = model.getCellSet();
		List<CellSetAxis> axes = cellSet.getAxes();
		CellSetAxis rowsOrColumns = axes.get(axisPos);
		
		ph.removeHierarchy(rowsOrColumns.getAxisOrdinal(), model.getCube().getHierarchies().get(0));

	

		return renderModel(model);
	}
	
	@PUT
	@Path("/{axis}/placehierarchy/{hierarchy}")
	public String addHierarchy(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("hierarchy") String hierarchyName){

		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		OlapConnection connection = ei.getOlapConnection();

		
		QueryAdapter qa = new QueryAdapter(model);
		qa.initialize();
		
		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, connection);
		
		CellSet cellSet = model.getCellSet();
		List<CellSetAxis> axes = cellSet.getAxes();
		CellSetAxis rowsOrColumns = axes.get(axisPos);


		List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();
		hierarchies.add(model.getCube().getHierarchies().get(3));
		
		ph.placeHierarchies(rowsOrColumns.getAxisOrdinal(),hierarchies ,true);
		MdxStatement s = qa.updateQuery();
		model.setMdx(s.toMdx());

		return renderModel(model);
	}
	
	@PUT
	@Path("/{axis}/swaphierarchies/{hierarchy1}/{hierarchy2}")
	public String swapHierarchies(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("hierarchy1") int hierarchyPos1, @PathParam("hierarchy2") int hierarchyPos2){
		
		int firstPos;
		int lastPos;
		
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		OlapConnection connection = ei.getOlapConnection();

		if(hierarchyPos1<hierarchyPos2){
			firstPos = hierarchyPos1;
			lastPos = hierarchyPos2;
		}else{
			lastPos = hierarchyPos1;
			firstPos = hierarchyPos2;
		}
		
		QueryAdapter qa = new QueryAdapter(model);
		qa.initialize();
		
		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, connection);
		
		CellSet cellSet = model.getCellSet();
		List<CellSetAxis> axes = cellSet.getAxes();
		CellSetAxis rowsOrColumns = axes.get(axisPos);
		List<Hierarchy> hierarchies = rowsOrColumns.getAxisMetaData().getHierarchies();
		
		Hierarchy hierarchyLast = hierarchies.remove(lastPos);
		Hierarchy hierarchyFirst = hierarchies.remove(firstPos);
		
		
		hierarchies.add(hierarchyPos1, hierarchyLast);
		hierarchies.add(hierarchyPos2, hierarchyFirst);
				
		
		ph.placeHierarchies(rowsOrColumns.getAxisOrdinal(),hierarchies ,true);
		MdxStatement s = qa.updateQuery();
		model.setMdx(s.toMdx());

		return renderModel(model);
	}

}