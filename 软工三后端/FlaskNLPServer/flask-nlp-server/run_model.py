# -*- coding: utf-8 -*-
import time

from ltp import LTP
import tensorflow as tf
import numpy as np
from . import network

FLAGS = tf.app.flags.FLAGS


# embedding the position
def pos_embed(x):
    if x < -60:
        return 0
    if -60 <= x <= 60:
        return x + 61
    if x > 60:
        return 122


def extract_entities(ltp, doc_path):
    entity_dict = {}
    file = open(doc_path, encoding='utf-8')
    text = file.readlines()
    file_len = len(text)
    cnt = 0
    for line in text:
        cnt = cnt + 1
        print(cnt, '/', file_len)
        line = line.replace('\n', '').replace('\r', '')
        if len(line) == 0:
            continue
        seg, hidden = ltp.seg([line])
        ner = ltp.ner(hidden)
        for tag, start, end in ner[0]:
            if tag == 'Nh':
                entity = "".join(seg[0][start:end + 1])
                if entity not in entity_dict.keys():
                    entity_dict[entity] = 1
                else:
                    entity_dict[entity] = entity_dict[entity] + 1
    entity_dict = sorted(entity_dict.items(), key=lambda kv: (kv[1], kv[0]), reverse=True)
    entity_res_list = []
    for entity_info in entity_dict:
        if entity_info[1] > 2:
            entity_res_list.append(entity_info[0])
    return entity_res_list


def relation_calculate_predict(relation_list):
    relation_predict_dict = {}
    for i in range(len(relation_list)):
        print(i + 1, '/', len(relation_list))
        relation_info = relation_list[i]
        entity_tuple = ()
        if (relation_info[0], relation_info[1]) in relation_predict_dict.keys() or (
            relation_info[1], relation_info[0]) in relation_predict_dict.keys():
            if (relation_info[0], relation_info[1]) in relation_predict_dict.keys():
                entity_tuple = (relation_info[0], relation_info[1])
                relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                    relation_predict_dict[entity_tuple][relation_info[3]][1] * \
                    relation_predict_dict[entity_tuple][relation_info[3]][0]
                relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                    relation_predict_dict[entity_tuple][relation_info[3]][1] + float(relation_info[2])
                relation_predict_dict[entity_tuple][relation_info[3]][0] = \
                    relation_predict_dict[entity_tuple][relation_info[3]][0] + 1
                relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                    relation_predict_dict[entity_tuple][relation_info[3]][1] / \
                    relation_predict_dict[entity_tuple][relation_info[3]][0]
                if float(relation_info[2]) > relation_predict_dict[entity_tuple][relation_info[3]][2]:
                    relation_predict_dict[entity_tuple][relation_info[3]][2] = float(relation_info[2])
            else:
                entity_tuple = (relation_info[1], relation_info[0])
                relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                    relation_predict_dict[entity_tuple][relation_info[3]][1] * \
                    relation_predict_dict[entity_tuple][relation_info[3]][0]
                relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                    relation_predict_dict[entity_tuple][relation_info[3]][1] + float(relation_info[2])
                relation_predict_dict[entity_tuple][relation_info[3]][0] = \
                    relation_predict_dict[entity_tuple][relation_info[3]][0] + 1
                relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                    relation_predict_dict[entity_tuple][relation_info[3]][1] / \
                    relation_predict_dict[entity_tuple][relation_info[3]][0]
                if float(relation_info[2]) > relation_predict_dict[entity_tuple][relation_info[3]][2]:
                    relation_predict_dict[entity_tuple][relation_info[3]][2] = float(relation_info[2])
        else:
            entity_tuple = (relation_info[0], relation_info[1])
            relation_predict_dict[entity_tuple] = {'unknown': [0, 0, 0], '亲子': [0, 0, 0], '夫妻': [0, 0, 0],
                                                   '师生': [0, 0, 0], '兄弟姐妹': [0, 0, 0], '合作': [0, 0, 0], '情侣': [0, 0, 0],
                                                   '祖孙': [0, 0, 0], '好友': [0, 0, 0], '亲戚': [0, 0, 0], '同门': [0, 0, 0],
                                                   '上下级': [0, 0, 0]}
            relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                relation_predict_dict[entity_tuple][relation_info[3]][1] * \
                relation_predict_dict[entity_tuple][relation_info[3]][0]
            relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                relation_predict_dict[entity_tuple][relation_info[3]][1] + float(relation_info[2])
            relation_predict_dict[entity_tuple][relation_info[3]][0] = \
                relation_predict_dict[entity_tuple][relation_info[3]][0] + 1
            relation_predict_dict[entity_tuple][relation_info[3]][1] = \
                relation_predict_dict[entity_tuple][relation_info[3]][1] / \
                relation_predict_dict[entity_tuple][relation_info[3]][0]
            if float(relation_info[2]) > relation_predict_dict[entity_tuple][relation_info[3]][2]:
                relation_predict_dict[entity_tuple][relation_info[3]][2] = float(relation_info[2])
    for key in relation_predict_dict.keys():
        relation_predict_dict[key] = sorted(relation_predict_dict[key].items(), key=lambda kv: (kv[1], kv[0]),
                                            reverse=True)
    # for key in relation_predict_dict.keys():
    #     print(key, relation_predict_dict[key])
    return relation_predict_dict


def extract(dict_path, doc_path):
    print('ltp is loading...')
    ltp = LTP()
    ltp.init_dict(path=dict_path, max_window=4)
    # If you retrain the model, please remember to change the path to your own model below:
    pathname = "model/nioCoin_ATT_GRU_model_doupo_shuihu-1100"

    wordembedding = np.load('model/vec.npy')
    test_settings = network.Settings()
    test_settings.vocab_size = 16693
    test_settings.num_classes = 12
    test_settings.big_num = 1

    with tf.Graph().as_default():
        sess = tf.Session()
        with sess.as_default():
            def test_step(word_batch, pos1_batch, pos2_batch, y_batch):

                feed_dict = {}
                total_shape = []
                total_num = 0
                total_word = []
                total_pos1 = []
                total_pos2 = []

                for i in range(len(word_batch)):
                    total_shape.append(total_num)
                    total_num += len(word_batch[i])
                    for word in word_batch[i]:
                        total_word.append(word)
                    for pos1 in pos1_batch[i]:
                        total_pos1.append(pos1)
                    for pos2 in pos2_batch[i]:
                        total_pos2.append(pos2)

                total_shape.append(total_num)
                total_shape = np.array(total_shape)
                total_word = np.array(total_word)
                total_pos1 = np.array(total_pos1)
                total_pos2 = np.array(total_pos2)

                feed_dict[mtest.total_shape] = total_shape
                feed_dict[mtest.input_word] = total_word
                feed_dict[mtest.input_pos1] = total_pos1
                feed_dict[mtest.input_pos2] = total_pos2
                feed_dict[mtest.input_y] = y_batch

                loss, accuracy, prob = sess.run(
                    [mtest.loss, mtest.accuracy, mtest.prob], feed_dict)
                return prob, accuracy

            with tf.variable_scope("model"):
                mtest = network.GRU(is_training=False, word_embeddings=wordembedding, settings=test_settings)

            names_to_vars = {v.op.name: v for v in tf.global_variables()}
            saver = tf.train.Saver(names_to_vars)
            saver.restore(sess, pathname)

            print('reading word embedding data...')
            vec = []
            word2id = {}
            f = open('model/vec.txt', encoding='utf-8')
            content = f.readline()
            content = content.strip().split()
            dim = int(content[1])
            while True:
                content = f.readline()
                if content == '':
                    break
                content = content.strip().split()
                word2id[content[0]] = len(word2id)
                content = content[1:]
                content = [(float)(i) for i in content]
                vec.append(content)
            f.close()
            word2id['UNK'] = len(word2id)
            word2id['BLANK'] = len(word2id)

            print('reading relation to id')
            relation2id = {}
            id2relation = {}
            f = open('model/relation2id.txt', 'r', encoding='utf-8')
            while True:
                content = f.readline()
                if content == '':
                    break
                content = content.strip().split()
                relation2id[content[0]] = int(content[1])
                id2relation[int(content[1])] = content[0]
            f.close()

            file = open(doc_path, encoding='utf-8')
            print(doc_path)
            line = file.readline()
            entity_list = []
            relation_list = []
            print('entity dict is loading...')
            entity_file = open(dict_path, encoding='utf-8')
            entity = entity_file.readline()
            while entity:
                if len(entity) > 0 and (entity != '\n' or entity != '\r' or entity != '\n\r'):
                    entity_list.append(entity.replace('\n', '').replace('\r', ''))
                entity = entity_file.readline()
            entity_file.close()
            start_time = time.time()
            print('entity extracting starts...')
            ltp_entity_list = extract_entities(ltp, doc_path)
            print('entity extracting success')
            end_time = time.time()
            print('entity extracting costs', end_time - start_time)
            for ltp_entity in ltp_entity_list:
                if ltp_entity not in entity_list:
                    entity_list.append(ltp_entity)
            print('entity_list:', entity_list)
            print('entity dict is writing...')
            entity_file = open(dict_path, 'w', encoding='utf-8')
            for entity in entity_list:
                entity_file.write(entity + '\n')
            entity_file.close()
            print('entity dict writes success')
            while line:
                # try:
                # BUG: Encoding error if user input directly from command line.
                if len(line) == 0 or line == '\n' or line == '\r' or line == '\n\r':
                    line = file.readline()
                    continue
                entity_find_list = []
                print('line:', line)
                segment, _ = ltp.seg([line])
                for seg_item in segment[0]:
                    if seg_item in entity_list and seg_item not in entity_find_list:
                        entity_find_list.append(seg_item)
                print('entity_find_list:', entity_find_list)
                sentence = line
                line = file.readline()
                if len(entity_find_list) > 1:
                    for k_1 in range(len(entity_find_list)):
                        for k_2 in range(k_1 + 1, len(entity_find_list)):
                            en1 = entity_find_list[k_1]
                            en2 = entity_find_list[k_2]
                            print("实体1: " + en1)
                            print("实体2: " + en2)
                            print(sentence)
                            relation = 0
                            en1pos = sentence.find(en1)
                            if en1pos == -1:
                                en1pos = 0
                            en2pos = sentence.find(en2)
                            if en2pos == -1:
                                en2post = 0
                            output = []
                            # length of sentence is 70
                            fixlen = 70
                            # max length of position embedding is 60 (-60~+60)
                            maxlen = 60

                            # Encoding test x
                            for i in range(fixlen):
                                word = word2id['BLANK']
                                rel_e1 = pos_embed(i - en1pos)
                                rel_e2 = pos_embed(i - en2pos)
                                output.append([word, rel_e1, rel_e2])

                            for i in range(min(fixlen, len(sentence))):

                                word = 0
                                if sentence[i] not in word2id:
                                    # print(sentence[i])
                                    # print('==')
                                    word = word2id['UNK']
                                    # print(word)
                                else:
                                    # print(sentence[i])
                                    # print('||')
                                    word = word2id[sentence[i]]
                                    # print(word)

                                output[i][0] = word
                            test_x = []
                            test_x.append([output])

                            # Encoding test y
                            label = [0 for i in range(len(relation2id))]
                            label[0] = 1
                            test_y = []
                            test_y.append(label)

                            test_x = np.array(test_x)
                            test_y = np.array(test_y)

                            test_word = []
                            test_pos1 = []
                            test_pos2 = []

                            for i in range(len(test_x)):
                                word = []
                                pos1 = []
                                pos2 = []
                                for j in test_x[i]:
                                    temp_word = []
                                    temp_pos1 = []
                                    temp_pos2 = []
                                    for k in j:
                                        temp_word.append(k[0])
                                        temp_pos1.append(k[1])
                                        temp_pos2.append(k[2])
                                    word.append(temp_word)
                                    pos1.append(temp_pos1)
                                    pos2.append(temp_pos2)
                                test_word.append(word)
                                test_pos1.append(pos1)
                                test_pos2.append(pos2)

                            test_word = np.array(test_word)
                            test_pos1 = np.array(test_pos1)
                            test_pos2 = np.array(test_pos2)

                            # print("test_word Matrix:")
                            # print(test_word)
                            # print("test_pos1 Matrix:")
                            # print(test_pos1)
                            # print("test_pos2 Matrix:")
                            # print(test_pos2)

                            prob, accuracy = test_step(test_word, test_pos1, test_pos2, test_y)
                            prob = np.reshape(np.array(prob), (1, test_settings.num_classes))[0]
                            print("关系是:")
                            # print(prob)
                            top3_id = prob.argsort()[-3:][::-1]
                            for n, rel_id in enumerate(top3_id):
                                print("No." + str(n + 1) + ": " + id2relation[rel_id] + ", Probability is " + str(
                                    prob[rel_id]))
                                relation_list.append([en1, en2, str(prob[rel_id]), id2relation[rel_id]])
            print('relation_list:', relation_list)
            print('system is calculating the predicting results')
            relation_predict_dict = relation_calculate_predict(relation_list)
            print('relation_predict_dict:', relation_predict_dict)
            spo_list = []
            for key in relation_predict_dict.keys():
                subject_name = key[0]
                object_name = key[1]
                relation = ''
                predict_list = relation_predict_dict[key]
                flag_sure = False
                for predict_tuple in predict_list:
                    if predict_tuple[1][2] > 0.999:
                        relation = predict_tuple[0]
                        flag_sure = True
                        break
                if not flag_sure:
                    if predict_list[0][1][1] < 0.6 or predict_list[0][1][0] < 2:
                        relation = 'unknown'
                    else:
                        relation = predict_list[0][0]
                spo_list.append([subject_name, relation, object_name])
            entity_with_relation_list = []
            entity_without_relation_list = []
            for spo in spo_list:
                if spo[0] not in entity_with_relation_list:
                    entity_with_relation_list.append(spo[0])
                if spo[2] not in entity_with_relation_list:
                    entity_with_relation_list.append(spo[2])
                print(spo)
            for entity in entity_list:
                if entity not in entity_with_relation_list:
                    entity_without_relation_list.append(entity)
            print('entity_list:', len(entity_list), entity_list)
            print('entity_with_relation_list:', len(entity_with_relation_list), entity_with_relation_list)
            print('entity_without_relation_list:', len(entity_without_relation_list), entity_without_relation_list)
            return spo_list, entity_list


if __name__ == "__main__":
    extract('dict.txt', 'doc.txt')
