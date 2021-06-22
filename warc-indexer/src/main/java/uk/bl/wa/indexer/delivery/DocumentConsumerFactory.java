/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package uk.bl.wa.indexer.delivery;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A Documentconsumer is responsible for receiving {@link uk.bl.wa.solr.SolrRecord}s and passing them on to a
 * receiving system, such as Solr, Elasticsearch or the file system.
 */
public class DocumentConsumerFactory {
    private static final Logger log = LoggerFactory.getLogger(DocumentConsumerFactory.class);

    /**
     * Create a DocumentConsumer based on the given configuration.
     * Exactly one of outputdir, solrURL or elasticURL must be defined.
     * @param conf base setup for the DocumentConsumer.
     * @param outputFolder if defined the DocumentConsumer will write to a local file in the folder.
     * @param outputGZIP if true and outputDir is != null, the output will be gzipped.
     * @param solrURL if defined the DocumentConsumer will send to Solr.
     * @param elasticURL if defined the Documentconsumer will send to Elasticsearch.
     * @param maxDocumentsOverride  if not null, this will override the value from conf "warc.solr.batch_size"
     * @param maxBytesOverride if not null, this will override the value from conf "warc.solr.batch_bytes"
     * @param disableCommitOverride if not null, this will override the value from conf "warc.solr.disablecommit"
     * @return a DocumentConsumer ready for consumption.
     * @throws IOException if the consumer could not be constructed.
     * @throws IllegalArgumentException if it was not possible to derive a proper setup.
     */
    public static DocumentConsumer createConsumer(
            Config conf, String outputFolder, Boolean outputGZIP, String solrURL, String elasticURL,
            Integer maxDocumentsOverride, Long maxBytesOverride, Boolean disableCommitOverride) throws IOException {
        int outputs = (outputFolder == null ? 0 : 1) + (solrURL == null ? 0 : 1) + (elasticURL == null ? 0 : 1);
        if (outputs > 1) {
            throw new IllegalArgumentException("Only 1 of either output, solr or elastic must be specified");
        }
        if (outputFolder != null) {
            return new FilesystemDocumentConsumer(
                    outputFolder, conf, outputGZIP, maxDocumentsOverride, maxBytesOverride, disableCommitOverride);
        }
        if (solrURL != null) {
            return new SolrDocumentConsumer(
                    solrURL, conf, maxDocumentsOverride, maxBytesOverride, disableCommitOverride);
        }
        if (elasticURL != null) {
            return new ElasticsearchDocumentConsumer(
                    elasticURL, conf, maxDocumentsOverride, maxBytesOverride, disableCommitOverride);
        }
        throw new IllegalArgumentException("Either output, solr or elastic must be specified");
    }
}
