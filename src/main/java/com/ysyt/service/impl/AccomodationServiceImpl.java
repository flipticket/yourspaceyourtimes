
package com.ysyt.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Util.Util;
import com.ysyt.bean.Accomodations;
import com.ysyt.bean.AccomodationsDetails;
import com.ysyt.bean.AmenitiesMapping;
import com.ysyt.bean.AttributesMaster;
import com.ysyt.bean.LocationBean;
import com.ysyt.dao.IAccomodationDao;
import com.ysyt.service.IAccomodationService;
import com.ysyt.to.request.AccomodationRequest;
import com.ysyt.to.request.AmenitiesMasterRequest;
import com.ysyt.to.request.LocationRequest;

@Service
@Transactional
public class AccomodationServiceImpl implements IAccomodationService {
	
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private HttpServletRequest httpRequest;
	
	@Autowired
	private IAccomodationDao iAccomodationDao;

	@Override
	public List<AttributesMaster> getAmenitiesParent(
			AmenitiesMasterRequest request) {
		
		
		
		return iAccomodationDao.getAmenitiesParent(request,sessionFactory);
	}

	
	@Override
	public AttributesMaster createAttributes(AttributesMaster oldAttributeBean) {
		
		if(!Util.isNull(oldAttributeBean.getId())){
			AttributesMaster currentAttributeBean = getAttributeMasterById(oldAttributeBean.getId());
		
			if(!Util.isNull(currentAttributeBean)){			
				oldAttributeBean = updateAttributeDetails(oldAttributeBean,currentAttributeBean);
			}
		}
		
		oldAttributeBean = iAccomodationDao.createOrUpdateAttributeMaster(oldAttributeBean,sessionFactory);
		
		return oldAttributeBean;
	}
	
	
	@Override
	public AttributesMaster getAttributeMasterById(Long id) {
		
		return iAccomodationDao.getAttributeMasterById(id,sessionFactory);

	}

	private AttributesMaster updateAttributeDetails(AttributesMaster oldAttributesMaster,
			AttributesMaster currentAttributesMaster) {
		
		if(!Util.isNull(currentAttributesMaster.getTitle())){
			oldAttributesMaster.setTitle(currentAttributesMaster.getTitle());
		}
		
		if(!Util.isNull(currentAttributesMaster.getDescription())){
			oldAttributesMaster.setDescription(currentAttributesMaster.getDescription());
		}
		
		if(!Util.isNull(currentAttributesMaster.getIsAmenities())){
			oldAttributesMaster.setIsAmenities(currentAttributesMaster.getIsAmenities());
		}
		
		if(!Util.isNull(currentAttributesMaster.getIsDeleted())){
			oldAttributesMaster.setIsDeleted(currentAttributesMaster.getIsDeleted());
		}
		
		if(!Util.isNull(currentAttributesMaster.getParentId())){
			oldAttributesMaster.setParentId(currentAttributesMaster.getParentId());
		}
			
		
		return oldAttributesMaster;
	}


	@Override
	public AmenitiesMapping createAmenitiyMapping(AmenitiesMapping oldAttributeBean) {
		
		if(!Util.isNull(oldAttributeBean.getId())){
			AmenitiesMapping currentAttributeBean = getAmenitiesMappingById(oldAttributeBean.getId());
		
			if(!Util.isNull(currentAttributeBean)){			
				oldAttributeBean = updateAmenitiesMappingDetails(oldAttributeBean,currentAttributeBean);
			}
		}
		
		oldAttributeBean = iAccomodationDao.createOrUpdateAmenitiesMapping(oldAttributeBean,sessionFactory);
		
		return oldAttributeBean;
		
	}


	private AmenitiesMapping updateAmenitiesMappingDetails(
			AmenitiesMapping currentAttributeBean,
			AmenitiesMapping oldAttributeBean) {
		
		if(!Util.isNull(currentAttributeBean.getAttributeId())){
			oldAttributeBean.setAttributeId(currentAttributeBean.getAttributeId());
		}
		
		if(!Util.isNull(currentAttributeBean.getParentAmenitiyId())){
			oldAttributeBean.setParentAmenitiyId(currentAttributeBean.getParentAmenitiyId());
		}
		if(!Util.isNull(currentAttributeBean.getIsDeleted())){
			oldAttributeBean.setIsDeleted(currentAttributeBean.getIsDeleted());
		}
				
		return oldAttributeBean;
	}

	private AmenitiesMapping getAmenitiesMappingById(Long id) {
		
		return iAccomodationDao.getAmenitiesMappingById(id,sessionFactory);
	}


	@Override
	public Map<String, List<AttributesMaster>> getAmenitiesList(Long typeId,String sourceName) {

		Map<String, List<AttributesMaster>> mapAmenities = new HashMap<>();
		List<AmenitiesMapping> amenitiesMapping = new ArrayList<>();
		Set<String> parentAmenities = new  HashSet<>();
		List<AttributesMaster> attributeMaster = null;
		
		amenitiesMapping = iAccomodationDao.getAmenitiesMappingList(typeId,sourceName, sessionFactory);
		
		for(AmenitiesMapping amenitiesSet : amenitiesMapping){
			parentAmenities.add(amenitiesSet.getParentBean().getTitle());
		}
		
		for(String parentAmenity : parentAmenities){
			
			attributeMaster = new ArrayList<>();
			
			for(AmenitiesMapping amenities : amenitiesMapping){
				
				if(parentAmenity.equals(amenities.getParentBean().getTitle())){
					
					attributeMaster.add(amenities.getAttributeBean());					
				}
				
			}
			mapAmenities.put(parentAmenity, attributeMaster);
		}
		
		return mapAmenities;
	}


	@Override
	public Accomodations createAccomodation(AccomodationRequest request) {
		
		Accomodations oldAccomodation = request.getAccomodation();
		
		if(!Util.isNull(oldAccomodation.getId())){
			Accomodations currentAccomodation = getAccomodationById(oldAccomodation.getId());
		
			if(!Util.isNull(currentAccomodation)){			
				oldAccomodation = updateAccomodationDetails(oldAccomodation,currentAccomodation);
			}else{
				Util.throwPrimeException("Resource Not Available");
			}
		}else{
			oldAccomodation.setCreatedAt(Util.getCurrentTimeStamp());
			oldAccomodation.setCreatedBy(Util.getUserId(httpRequest));
		}

		oldAccomodation.setUpdatedAt(Util.getCurrentTimeStamp());
		oldAccomodation.setUpdatedBy(Util.getUserId(httpRequest));
		
		oldAccomodation = iAccomodationDao.createOrUpdateAccomodation(oldAccomodation,sessionFactory);
		
		if(!Util.isNull(oldAccomodation)){
			
			if(!Util.isNullList(request.getAccomodation().getAccomodationDetails())){
				
				for(AccomodationsDetails acccomodationDetails : request.getAccomodation().getAccomodationDetails()){
				
					if(!Util.isNull(acccomodationDetails.getId())){
						AccomodationsDetails currentAccomodationDetails = getAccomodationDetailsById(acccomodationDetails.getId());
					
						if(!Util.isNull(currentAccomodationDetails)){			
							acccomodationDetails = updateExtraAccomodationDetailBean(acccomodationDetails,currentAccomodationDetails);
						}else{
							Util.throwPrimeException("Extra Details Resource Not Available");
						}
					}else{
						acccomodationDetails.setCreatedAt(Util.getCurrentTimeStamp());
						acccomodationDetails.setCreatedBy(Util.getUserId(httpRequest));
						acccomodationDetails.setSourceId(request.getAccomodation().getId());
						acccomodationDetails.setSourceType("accomodation");
					}
					
					acccomodationDetails.setUpdatedAt(Util.getCurrentTimeStamp());
					acccomodationDetails.setUpdatedBy(Util.getUserId(httpRequest));
					
					iAccomodationDao.createOrUpdateAccomodationDetails(acccomodationDetails,sessionFactory);
					
				}
				
			}
			
		}
		
				
		return oldAccomodation;
		
	}


	private AccomodationsDetails updateExtraAccomodationDetailBean(
			AccomodationsDetails acccomodationDetails,
			AccomodationsDetails currentAccomodationDetails) {
		

		if(!Util.isNull(acccomodationDetails.getAttributeId())){
			currentAccomodationDetails.setAttributeId(acccomodationDetails.getAttributeId());
		}
		
		if(!Util.isNull(acccomodationDetails.getIsAmenities())){
			currentAccomodationDetails.setIsAmenities(acccomodationDetails.getIsAmenities());
		}

		if(!Util.isNull(acccomodationDetails.getIsDeleted())){
			currentAccomodationDetails.setIsDeleted(acccomodationDetails.getIsDeleted());
		}
		
		if(!Util.isNull(acccomodationDetails.getOrderId())){
			currentAccomodationDetails.setOrderId(acccomodationDetails.getOrderId());
		}
		
		
		if(!Util.isNull(acccomodationDetails.getParentId())){
			currentAccomodationDetails.setParentId(acccomodationDetails.getParentId());
		}
		
		if(!Util.isNull(acccomodationDetails.getSourceId())){
			currentAccomodationDetails.setParentId(acccomodationDetails.getSourceId());
		}
		
		if(!Util.isNull(acccomodationDetails.getSourceType())){
			currentAccomodationDetails.setSourceType(acccomodationDetails.getSourceType());
		}
		
		if(!Util.isNull(acccomodationDetails.getValue())){
			currentAccomodationDetails.setValue(acccomodationDetails.getValue());
		}
		
		
		return currentAccomodationDetails;
		
	}


	private AccomodationsDetails getAccomodationDetailsById(Long id) {
		
		return iAccomodationDao.getAccomodationDetailsById(id, sessionFactory);
	}


	private Accomodations updateAccomodationDetails(
			Accomodations oldAccomodation, Accomodations currentAccomodation) {
		
		if(!Util.isNull(currentAccomodation.getTitle())){
			oldAccomodation.setTitle(currentAccomodation.getTitle());
		}
		
		if(!Util.isNull(currentAccomodation.getDescription())){
			oldAccomodation.setDescription(currentAccomodation.getDescription());
		}
		
		if(!Util.isNull(currentAccomodation.getAddress())){
			oldAccomodation.setAddress(currentAccomodation.getAddress());
		}
		
		if(!Util.isNull(currentAccomodation.getLocationId())){
			oldAccomodation.setLocationId(currentAccomodation.getLocationId());
		}
		
		if(!Util.isNull(currentAccomodation.getCost())){
			oldAccomodation.setCost(currentAccomodation.getCost());
		}
		
		if(!Util.isNull(currentAccomodation.getCurrentRoomCount())){
			oldAccomodation.setCurrentRoomCount(currentAccomodation.getCurrentRoomCount());
		}
		
		if(!Util.isNull(currentAccomodation.getIsDeleted())){
			oldAccomodation.setIsDeleted(currentAccomodation.getIsDeleted());
		}
		
		if(!Util.isNull(currentAccomodation.getLatitude())){
			oldAccomodation.setLatitude(currentAccomodation.getLatitude());
		}
		
		if(!Util.isNull(currentAccomodation.getLongitude())){
			oldAccomodation.setLongitude(currentAccomodation.getLongitude());
		}
		
		if(!Util.isNull(currentAccomodation.getTotalRoomCount())){
			oldAccomodation.setTotalRoomCount(currentAccomodation.getTotalRoomCount());
		}
		if(!Util.isNull(currentAccomodation.getTypeId())){
			oldAccomodation.setTypeId(currentAccomodation.getTypeId());
		}
		
		
		return oldAccomodation;
		
	}


	private Accomodations getAccomodationById(Long id) {
		
		return iAccomodationDao.getAccomodatoinById(id,sessionFactory);
		
	}


	@Override
	public List<LocationBean> getLocationDetails(LocationRequest request) {
		
		return iAccomodationDao.getLocationDetails(request,sessionFactory);
	}
	
	

}
