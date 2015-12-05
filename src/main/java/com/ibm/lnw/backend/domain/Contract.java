package com.ibm.lnw.backend.domain;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Jan Valentik on 11/29/2015.
 */
@Stateless
public class Contract {
	@Resource(lookup = "jdbc/LNWTool-sqldb")
	private DataSource dataSource;
	private String wbsId;
	private String pmaNotesId;
	private String peNotes;
	private String contractNumber;
	private String sapContract;
	private String customerName;


	public void findContractByWBS(String wbsId) throws Exception {
		System.out.println("Looking for: " + wbsId);
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		if (dataSource == null) {
			throw new Exception("DB not connected");
		}
		try {
			String query = "SELECT WBS_ID, PMA_NOTES, LEGAL_CONTRACT, CUSTOMER_NAME, SAP_CONTRACT, PE_NOTES FROM " +
					"USER04064.CONTRACTS";
			con = dataSource.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			this.wbsId = "";
			while(rs.next()){
				if (wbsId.equals(rs.getString("WBS_ID"))) {
					System.out.println("RECORD FOUND");
					this.wbsId = rs.getString("WBS_ID");
					pmaNotesId = rs.getString("PMA_NOTES");
					peNotes = rs.getString("PE_NOTES");
					contractNumber = rs.getString("LEGAL_CONTRACT");
					sapContract = rs.getString("SAP_CONTRACT");
					customerName = rs.getString("CUSTOMER_NAME");
					break;
				}
			}
			if (this.wbsId.equals("")) {
				throw new Exception("Request not found");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public String getWbsId() {
		return wbsId;
	}

	public String getPmaNotesId() {
		return pmaNotesId;
	}

	public String getPeNotes() {
		return peNotes;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public String getSapContract() {
		return sapContract;
	}

	public String getCustomerName() {
		return customerName;
	}
}
