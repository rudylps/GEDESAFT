package filrouge.gedesaft.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import filrouge.gedesaft.model.ButtonModel;

@Repository
public class DataDaoImpl implements DataDaoInterface {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private DataSource datasource;

	@Autowired
	public DataDaoImpl(JdbcTemplate jdbcTemplate) {
		this.datasource = jdbcTemplate.getDataSource();
	}
	
	@Override
	public List<ButtonModel> getListButtonData(
			Long idRecherche, String idObjectButton, String nameObjectButton, String request)
			throws Exception {
		ButtonModel button;
		QueryResultButton buttonResult = new QueryResultButton ();
		PreparedStatement pstmt = null;
		ResultSet rs;
		List<ButtonModel> buttonList = new ArrayList<ButtonModel>();
		try {
			pstmt = datasource.getConnection().prepareStatement(request);
			pstmt.setLong(1, idRecherche);
			logSQL(pstmt);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				button = buttonResult.queryResult(rs, idObjectButton, nameObjectButton);				
				buttonList.add(button);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("SQL Error !:" + pstmt.toString(), e);
			throw e;
		} finally {
			pstmt.close();
		}
		return buttonList;
	}

	@Override
	public Object getObjectData(String objectType, Long idObject, String request) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs;
		QueryResultObjectFactory queryFactory = new QueryResultObjectFactory();
		Object objectResult = null;
		try {
			pstmt = datasource.getConnection().prepareStatement(request);
			pstmt.setLong(1, idObject);
			logSQL(pstmt);
			rs = pstmt.executeQuery();
			if (rs.next())
				objectResult = queryFactory.getQueryResultObject(objectType).queryResult(rs,idObject);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("SQL Error !:" + pstmt.toString(), e);
			throw e;
		} finally {
			pstmt.close();
		}
		return objectResult;
	}
	
	private void logSQL(PreparedStatement pstmt) {
		String sql;
		if (pstmt == null)
			return;
		sql = pstmt.toString().substring(pstmt.toString().indexOf(":") + 2);
		log.debug(sql);
	}
	
	@Bean
	public DaoVehicule createVehiculeDao(JdbcTemplate jdbcTemplate, DataSource datasource) {
		return new DaoVehicule(jdbcTemplate, datasource);
	}
	
}