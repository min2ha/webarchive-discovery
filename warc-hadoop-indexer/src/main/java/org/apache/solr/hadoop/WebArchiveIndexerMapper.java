///**
// * 
// */
//package org.apache.solr.hadoop;
//
//import java.io.IOException;
//import java.security.NoSuchAlgorithmException;
//import java.util.Properties;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.hadoop.io.Text;
//import org.apache.log4j.PropertyConfigurator;
//import org.archive.io.ArchiveRecordHeader;
//
//import uk.bl.wa.hadoop.WritableArchiveRecord;
//import uk.bl.wa.hadoop.indexer.WARCIndexerRunner;
//import uk.bl.wa.indexer.WARCIndexer;
//import uk.bl.wa.solr.SolrFields;
//import uk.bl.wa.solr.SolrRecord;
//
//import com.typesafe.config.Config;
//import com.typesafe.config.ConfigFactory;
//
///**
// * @author Andrew Jackson <Andrew.Jackson@bl.uk>
// *
// */
//public class WebArchiveIndexerMapper extends
//		SolrMapper<Text, WritableArchiveRecord> {
//	private static final Log LOG = LogFactory
//			.getLog(WebArchiveIndexerMapper.class);
//
//	private WARCIndexer windex;
//
//	public WebArchiveIndexerMapper() {
//		try {
//			Properties props = new Properties();
//			props.load(getClass().getResourceAsStream(
//					"/log4j-override.properties"));
//			PropertyConfigurator.configure(props);
//		} catch (IOException e1) {
//			LOG.error("Failed to load log4j config from properties file.");
//		}
//
//	}
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.apache.solr.hadoop.SolrMapper#setup(org.apache.hadoop.mapreduce.Mapper
//	 * .Context)
//	 */
//	@Override
//	protected void setup(Context context)
//			throws IOException, InterruptedException {
//		super.setup(context);
//
//		// Additional...
//		try {
//			// Get config from job property:
//			Config config = ConfigFactory.parseString(context
//					.getConfiguration()
//					.get(WARCIndexerRunner.CONFIG_PROPERTIES));
//			// Initialise indexer:
//			this.windex = new WARCIndexer(config);
//			// Re-configure logging:
//		} catch (NoSuchAlgorithmException e) {
//			LOG.error("ArchiveTikaMapper.configure(): " + e.getMessage());
//		}
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.apache.hadoop.mapreduce.Mapper#map(java.lang.Object,
//	 * java.lang.Object, org.apache.hadoop.mapreduce.Mapper.Context)
//	 */
//	@Override
//	protected void map(Text key, WritableArchiveRecord value, Context context)
//			throws IOException, InterruptedException {
//		ArchiveRecordHeader header = value.getRecord().getHeader();
//
//		if (!header.getHeaderFields().isEmpty()) {
//			SolrRecord solr = windex.extract(key.toString(), value.getRecord());
//
//			if (solr == null) {
//				LOG.debug("WARCIndexer returned NULL for: " + header.getUrl());
//				return;
//			}
//
//			String host = (String) solr.getFieldValue(SolrFields.SOLR_HOST);
//			if (host == null) {
//				host = "unknown.host";
//			}
//
//			Text oKey = new Text(host);
//			try {
//				SolrInputDocumentWritable wsolr = new SolrInputDocumentWritable(
//						solr.getSolrDocument());
//				context.write(oKey, wsolr);
//			} catch (Exception e) {
//				LOG.error(e.getClass().getName() + ": " + e.getMessage() + "; "
//						+ header.getUrl() + "; " + oKey + "; " + solr);
//			}
//		}
//	}
//
// }
