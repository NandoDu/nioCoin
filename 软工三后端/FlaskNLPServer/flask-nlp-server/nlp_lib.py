import functools
import time
import json
from pathlib import Path
from abc import abstractmethod
from typing import Dict, List
from .run_model import extract

tmp = Path('./tmp-process')
tmp.mkdir(exist_ok = True)

def get_entity_dict(id):
    return tmp / f'entity_dict_{id}.txt'


def get_document(id):
    return tmp / f'document_{id}.txt'


class NreHint:
    def __init__(self):
        self.time = time.time()

    @abstractmethod
    def use(self, triple):
        return triple
        
    @abstractmethod
    def use_for_entity(self, entity):
        return entity
    

    def preprocess(self, id):
        pass


class UnknownHint(NreHint):
    def use(self, triple):
        head, rel, tail = triple
        if head == tail:
            return None
        if rel == 'unknown':
            print ('unknown')
            return None
        return triple


class LabelModHint(NreHint):
    def __init__(self, old, new):
        super().__init__()
        self.old = old
        self.new = new

    def use(self, triple):
        def try_replace(entity):
            if entity == self.old:
                print('replace node')
                return self.new
            return entity

        head, rel, tail = triple
        return [try_replace(head), rel, try_replace(tail)]
        
    def use_for_entity(self, entity):
        if entity == self.old:
            return self.new
        return entity


class LabelRmHint(NreHint):
    def __init__(self, label):

        super().__init__()
        self.label = label

    def use(self, triple):
        head, rel, tail = triple
        if head == self.label or tail == self.label:
            print('remove node')
            return None
        return triple
    
    def use_for_entity(self, entity):
        if entity == self.label:
            return None
        return entity
    


class RelModHint(NreHint):
    def __init__(self, head, tail, old, new):
        super().__init__()
        self.head = head
        self.tail = tail
        self.old = old
        self.new = new

    def use(self, triple):
        head, rel, tail = triple
        if head == self.head and rel == self.old and tail == self.tail:
            print('replace relation')
            return [head, self.new, tail]
        return triple


class RelRmHint(NreHint):
    def __init__(self, head, tail, rel):
        super().__init__()
        self.head = head
        self.tail = tail
        self.rel = rel

    def use(self, triple):
        head, rel, tail = triple
        if head == self.head and rel == self.rel and tail == self.tail:
            print('remove relation')
            return None
        return triple


class LabelNewHint(NreHint):
    def __init__(self, label):
        super().__init__()
        self.label = label

    def use(self, triple):
        return triple
        pass

    def preprocess(self, id):
        super().preprocess(id)
        entity_dict = get_entity_dict(id)
        with open(entity_dict, 'a',encoding='utf-8') as f:
            f.write(self.label + '\n')


class Hints:
    def __init__(self, id):
        self.id = id
        self.hints: List[NreHint] = [UnknownHint()]

    def label_mod(self, old, new):
        self.hints.append(LabelModHint(old, new))

    def label_rm(self, label):
        self.hints.append(LabelRmHint(label))

    def rel_mod(self, head, old, new, tail):
        self.hints.append(RelModHint(head, tail, old, new))

    def label_new(self, label):
        self.hints.append(LabelNewHint(label))

    def rel_rm(self, head, rel, tail):
        self.hints.append(RelRmHint(head, tail, rel))

    def _filter(self, triples):
        def opt(triples, hint: NreHint):
            triples = [hint.use(triple) for triple in triples]
            triples = [triple for triple in triples if triple is not None]
            return triples

        triples = functools.reduce(opt, self.hints, triples)
        return triples
    
    def _entity_filter(self, entities):
        def opt(entities, hint: NreHint):
            entities = [hint.use_for_entity(entity) for entity in entities]
            entities = [entity for entity in entities if entity is not None]
            return entities
        
        entities = functools.reduce(opt, self.hints, entities)
        return entities
        

    def _preprocess(self, id):
        for hint in self.hints:
            hint.preprocess(id)

    def nre(self, text):
        en_dict_path = get_entity_dict(self.id)
        doc_path = get_document(self.id)
        with doc_path.open('w',encoding='utf-8') as doc:
            doc.write(text)
        if len(self.hints) == 1:
            with en_dict_path.open('w',encoding='utf-8') as en:
                en.write('')

        self._preprocess(self.id)

        triples, standalone_list = extract(en_dict_path, doc_path)
        entity_set = set(standalone_list)
        
        for triple in triples:
            head, rel, tail = triple
            entity_set.add(head)
            entity_set.add(tail)
        
        triples = self._filter(triples)
        entity_list = self._entity_filter(list(entity_set))

        def adapt(triple):
            head, rel, tail = triple
            return {
                'node1': head,
                'relation': rel,
                'node2': tail,
            }

        msg = json.dumps({
            'graphId': self.id,
            'data': list(map(adapt, triples)),
            'entities': entity_list
        }, ensure_ascii=False)
        print(msg)
        return msg


class HintMap:
    def __init__(self):
        self._data: Dict[str, Hints] = {}

    def get(self, graph_id):
        if graph_id not in self._data:
            self._data[graph_id] = Hints(graph_id)
        return self._data[graph_id]


hint_map = HintMap()
