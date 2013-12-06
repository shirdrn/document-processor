
Introduction
==================

Process documents to prepare train/test data for 'libsvm' tool.
We are using CHI to select terms as the feature vector, and then using TF-IDF to compute weight values. 


How To
==================

Compute data for libsvm tool, include 2 phases: train and test.

* For train:
Program entrance class: org.shirdrn.document.processor.TrainDocumentProcessorDriver
Configuration file    : config-train.properties

* For test:
Program entrance class: org.shirdrn.document.processor.TestDocumentProcessorDriver
Configuration file    : config-test.properties


FAQ
==================

* If you choose ICTCLAS Chinese analyzer, be sure to copy file 'NLPIR_JNI.dll' to 
directory 'C:\Windows\System32' in Win7 operating system(default Win7 64bit, more about
ICTCLAS, please see also http://ictclas.nlpir.org/downloads). 


Contact
==================

* Website: www.shiyanjun.cn 
* Email  : shirdrn@gmail.com

